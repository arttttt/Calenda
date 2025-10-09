package com.arttttt.calenda.feature.agenda.data

import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.CalendarContract
import com.arttttt.calenda.common.domain.model.CalendarEvent
import com.arttttt.calenda.feature.agenda.domain.model.EventChange
import com.arttttt.calenda.feature.agenda.domain.repository.EventsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

@ContributesBinding(AppScope::class)
@Inject
class EventsRepositoryImpl(
    context: Context,
) : EventsRepository {

    companion object {
        private val DEBOUNCE_DELAY = 250.milliseconds

        private val EVENTS_URI: Uri = CalendarContract.Events.CONTENT_URI
        private val INSTANCES_URI: Uri = CalendarContract.Instances.CONTENT_URI
    }

    private data class InstanceSnapshot(
        val instanceId: Long,
        val event: CalendarEvent,
        val hash: Int,
    )

    private val contentResolver = context.contentResolver

    private val projection = arrayOf(
        CalendarContract.Instances._ID,
        CalendarContract.Instances.EVENT_ID,
        CalendarContract.Instances.CALENDAR_ID,
        CalendarContract.Instances.TITLE,
        CalendarContract.Instances.DESCRIPTION,
        CalendarContract.Instances.EVENT_LOCATION,
        CalendarContract.Instances.BEGIN,
        CalendarContract.Instances.END,
        CalendarContract.Instances.ALL_DAY,
        CalendarContract.Instances.CALENDAR_COLOR,
    )

    override suspend fun getEvents(
        calendarIds: Set<Long>,
        startTime: Long,
        endTime: Long,
    ): Result<List<CalendarEvent>> {
        return runCatching {
            withContext(Dispatchers.IO) {
                if (calendarIds.isEmpty()) {
                    return@withContext emptyList()
                }

                queryInstances(
                    calendarIds = calendarIds,
                    startTime = startTime,
                    endTime = endTime,
                )
                    .values
                    .map { it.event }
            }
        }
    }

    override fun observeEventChanges(
        calendarIds: Set<Long>,
        startTime: Long,
        endTime: Long,
        initialEvents: List<CalendarEvent>,
    ): Flow<List<EventChange>> {
        return callbackFlow {
            val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

            val trigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

            var lastSnapshot: Map<Long, InstanceSnapshot> = if (initialEvents.isNotEmpty()) {
                buildSnapshotFromEvents(initialEvents)
            } else {
                queryInstances(
                    calendarIds = calendarIds,
                    startTime = startTime,
                    endTime = endTime,
                )
            }

            val eventsObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean) {
                    trigger.tryEmit(Unit)
                }
            }

            val instancesObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean) {
                    trigger.tryEmit(Unit)
                }
            }

            contentResolver.registerContentObserver(
                EVENTS_URI,
                true,
                eventsObserver,
            )

            contentResolver.registerContentObserver(
                INSTANCES_URI,
                true,
                instancesObserver,
            )

            val job = scope.launch {
                trigger
                    .debounce(DEBOUNCE_DELAY)
                    .collect {
                        val freshSnapshot = queryInstances(
                            calendarIds = calendarIds,
                            startTime = startTime,
                            endTime = endTime,
                        )

                        val changes = computeDelta(
                            old = lastSnapshot,
                            new = freshSnapshot,
                        )

                        lastSnapshot = freshSnapshot

                        if (changes.isNotEmpty()) {
                            send(changes)
                        }
                    }
            }

            awaitClose {
                contentResolver.unregisterContentObserver(eventsObserver)
                contentResolver.unregisterContentObserver(instancesObserver)
                job.cancel()
            }
        }
    }

    private fun queryInstances(
        calendarIds: Set<Long>,
        startTime: Long,
        endTime: Long,
    ): Map<Long, InstanceSnapshot> {
        if (calendarIds.isEmpty()) {
            return emptyMap()
        }

        val uri = CalendarContract.Instances.CONTENT_URI.buildUpon().apply {
            appendPath(startTime.toString())
            appendPath(endTime.toString())
        }.build()

        val selection = "${CalendarContract.Instances.CALENDAR_ID} IN (${calendarIds.joinToString(",")}) " +
                "AND ${CalendarContract.Instances.STATUS} != ${CalendarContract.Instances.STATUS_CANCELED}"

        val map = LinkedHashMap<Long, InstanceSnapshot>()

        contentResolver.query(
            uri,
            projection,
            selection,
            null,
            "${CalendarContract.Instances.BEGIN} ASC, ${CalendarContract.Instances.END} - ${CalendarContract.Instances.BEGIN} DESC",
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(CalendarContract.Instances._ID)
            val eventIdColumn = cursor.getColumnIndexOrThrow(CalendarContract.Instances.EVENT_ID)
            val calendarIdColumn = cursor.getColumnIndexOrThrow(CalendarContract.Instances.CALENDAR_ID)
            val titleColumn = cursor.getColumnIndexOrThrow(CalendarContract.Instances.TITLE)
            val descriptionColumn = cursor.getColumnIndexOrThrow(CalendarContract.Instances.DESCRIPTION)
            val locationColumn = cursor.getColumnIndexOrThrow(CalendarContract.Instances.EVENT_LOCATION)
            val beginColumn = cursor.getColumnIndexOrThrow(CalendarContract.Instances.BEGIN)
            val endColumn = cursor.getColumnIndexOrThrow(CalendarContract.Instances.END)
            val allDayColumn = cursor.getColumnIndexOrThrow(CalendarContract.Instances.ALL_DAY)
            val colorColumn = cursor.getColumnIndexOrThrow(CalendarContract.Instances.CALENDAR_COLOR)

            while (cursor.moveToNext()) {
                val instanceId = cursor.getLong(idColumn)

                val event = cursor.getCalendarEvent(
                    idColumn = idColumn,
                    eventIdColumn = eventIdColumn,
                    calendarIdColumn = calendarIdColumn,
                    titleColumn = titleColumn,
                    descriptionColumn = descriptionColumn,
                    locationColumn = locationColumn,
                    beginColumn = beginColumn,
                    endColumn = endColumn,
                    allDayColumn = allDayColumn,
                    colorColumn = colorColumn,
                )

                val hash = computeEventHash(event)

                map[instanceId] = InstanceSnapshot(
                    instanceId = instanceId,
                    event = event,
                    hash = hash,
                )
            }
        }

        return map
    }

    private fun buildSnapshotFromEvents(events: List<CalendarEvent>): Map<Long, InstanceSnapshot> {
        return events.associate { event ->
            val instanceId = event.id
            val hash = computeEventHash(event)

            instanceId to InstanceSnapshot(
                instanceId = instanceId,
                event = event,
                hash = hash,
            )
        }
    }

    private fun computeDelta(
        old: Map<Long, InstanceSnapshot>,
        new: Map<Long, InstanceSnapshot>,
    ): List<EventChange> {
        val changes = mutableListOf<EventChange>()

        for ((key, snapshot) in new) {
            if (key !in old) {
                changes += EventChange.Added(snapshot.event)
            }
        }

        for ((key, snapshot) in old) {
            if (key !in new) {
                changes += EventChange.Removed(snapshot.event)
            }
        }

        for ((key, newSnapshot) in new) {
            val oldSnapshot = old[key] ?: continue
            if (oldSnapshot.hash != newSnapshot.hash) {
                changes += EventChange.Modified(
                    oldEvent = oldSnapshot.event,
                    event = newSnapshot.event,
                )
            }
        }

        return changes
    }

    private fun computeEventHash(event: CalendarEvent): Int {
        return arrayOf<Any?>(
            event.eventId,
            event.calendarId,
            event.title,
            event.description,
            event.location,
            event.startTime,
            event.endTime,
            event.isAllDay,
            event.color,
        ).contentHashCode()
    }

    private fun Cursor.getCalendarEvent(
        idColumn: Int,
        eventIdColumn: Int,
        calendarIdColumn: Int,
        titleColumn: Int,
        descriptionColumn: Int,
        locationColumn: Int,
        beginColumn: Int,
        endColumn: Int,
        allDayColumn: Int,
        colorColumn: Int,
    ): CalendarEvent {
        return CalendarEvent(
            id = getLong(idColumn),
            eventId = getLong(eventIdColumn),
            calendarId = getLong(calendarIdColumn),
            title = getString(titleColumn) ?: "Untitled",
            description = getString(descriptionColumn),
            location = getString(locationColumn),
            startTime = getLong(beginColumn),
            endTime = getLong(endColumn),
            isAllDay = getInt(allDayColumn) == 1,
            color = getInt(colorColumn),
        )
    }
}