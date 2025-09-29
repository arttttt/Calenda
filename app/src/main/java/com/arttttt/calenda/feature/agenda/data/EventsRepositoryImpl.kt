package com.arttttt.calenda.feature.agenda.data

import android.content.Context
import android.database.Cursor
import android.provider.CalendarContract
import com.arttttt.calenda.common.domain.model.CalendarEvent
import com.arttttt.calenda.feature.agenda.domain.repository.EventsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@ContributesBinding(AppScope::class)
@Inject
class EventsRepositoryImpl(
    context: Context,
) : EventsRepository {

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

                val uri = CalendarContract.Instances.CONTENT_URI.buildUpon().apply {
                    appendPath(startTime.toString())
                    appendPath(endTime.toString())
                }.build()

                val selection = "${CalendarContract.Instances.CALENDAR_ID} IN (${calendarIds.joinToString(",")}) " +
                        "AND ${CalendarContract.Instances.STATUS} != ${CalendarContract.Instances.STATUS_CANCELED}"

                val cursor = contentResolver.query(
                    uri,
                    projection,
                    selection,
                    null,
                    "${CalendarContract.Instances.BEGIN} ASC, ${CalendarContract.Instances.END} - ${CalendarContract.Instances.BEGIN} DESC",
                )

                buildList {
                    cursor?.use {
                        val idColumn = it.getColumnIndexOrThrow(CalendarContract.Instances._ID)
                        val eventIdColumn = it.getColumnIndexOrThrow(CalendarContract.Instances.EVENT_ID)
                        val calendarIdColumn = it.getColumnIndexOrThrow(CalendarContract.Instances.CALENDAR_ID)
                        val titleColumn = it.getColumnIndexOrThrow(CalendarContract.Instances.TITLE)
                        val descriptionColumn = it.getColumnIndexOrThrow(CalendarContract.Instances.DESCRIPTION)
                        val locationColumn = it.getColumnIndexOrThrow(CalendarContract.Instances.EVENT_LOCATION)
                        val beginColumn = it.getColumnIndexOrThrow(CalendarContract.Instances.BEGIN)
                        val endColumn = it.getColumnIndexOrThrow(CalendarContract.Instances.END)
                        val allDayColumn = it.getColumnIndexOrThrow(CalendarContract.Instances.ALL_DAY)
                        val colorColumn = it.getColumnIndexOrThrow(CalendarContract.Instances.CALENDAR_COLOR)

                        while (it.moveToNext()) {
                            this += it.getCalendarEvent(
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
                        }
                    }
                }
            }
        }
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