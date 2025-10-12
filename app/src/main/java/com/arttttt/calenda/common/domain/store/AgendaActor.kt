package com.arttttt.calenda.common.domain.store

import com.arttttt.calenda.common.domain.model.CalendarEvent
import com.arttttt.calenda.common.domain.repository.SelectedCalendarsRepository
import com.arttttt.calenda.common.domain.model.AgendaDay
import com.arttttt.calenda.common.domain.model.DateRange
import com.arttttt.calenda.common.domain.model.EventChange
import com.arttttt.calenda.common.domain.repository.EventsRepository
import com.arttttt.calenda.common.domain.strategy.AgendaLoadingStrategy
import com.arttttt.simplemvi.actor.DefaultActor
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

class AgendaActor(
    private val eventsRepository: EventsRepository,
    private val selectedCalendarsRepository: SelectedCalendarsRepository,
    private val loadingStrategy: AgendaLoadingStrategy,
) : DefaultActor<AgendaStore.Intent, AgendaStore.State, AgendaStore.SideEffect>() {

    private var observationJob: Job? = null

    override fun onInit() {
        observeSelectedCalendars()
    }

    override fun handleIntent(intent: AgendaStore.Intent) {
        when (intent) {
            is AgendaStore.Intent.LoadInitialData -> loadInitialData()
            is AgendaStore.Intent.LoadPreviousPage -> loadPreviousPage()
            is AgendaStore.Intent.LoadNextPage -> loadNextPage()
            is AgendaStore.Intent.ProcessCalendarChanges -> processCalendarChanges(intent.changes)
        }
    }

    private fun observeSelectedCalendars() {
        selectedCalendarsRepository
            .observeSelectedCalendars()
            .onEach { calendars ->
                reduce {
                    copy(selectedCalendars = calendars)
                }

                intent(AgendaStore.Intent.LoadInitialData)
            }
            .launchIn(scope)
    }

    private fun startObservingChanges(
        calendarIds: Set<Long>,
        startTime: Long,
        endTime: Long,
        initialEvents: List<CalendarEvent>,
    ) {
        observationJob?.cancel()

        observationJob = eventsRepository
            .observeEventChanges(
                calendarIds = calendarIds,
                startTime = startTime,
                endTime = endTime,
                initialEvents = initialEvents,
            )
            .onEach { changes ->
                intent(AgendaStore.Intent.ProcessCalendarChanges(changes))
            }
            .launchIn(scope)
    }

    private fun loadInitialData() {
        if (state.isLoading) return

        if (state.selectedCalendars.isEmpty()) {
            observationJob?.cancel()
            observationJob = null

            reduce {
                copy(
                    days = emptyList(),
                )
            }

            return
        }

        reduce {
            copy(
                isLoading = true,
            )
        }

        scope.launch {
            val range = if (state.days.isEmpty()) {
                loadingStrategy.calculateInitialRange(state.currentDate)
            } else {
                val existingStart = state.earliestDate ?: state.currentDate
                val existingEnd = state.latestDate ?: state.currentDate
                DateRange(
                    existingStart,
                    existingEnd
                )
            }

            val wasEmpty = state.days.isEmpty()

            eventsRepository
                .getEvents(
                    calendarIds = state.selectedCalendars,
                    startTime = range.startDate.toStartOfDayMillis(),
                    endTime = range.endDate.toEndOfDayMillis(),
                )
                .onSuccess { events ->
                    val days = groupEventsByDays(
                        events = events,
                        startDate = range.startDate,
                        endDate = range.endDate,
                    )

                    reduce {
                        copy(
                            days = days,
                            isLoading = false,
                        )
                    }

                    startObservingChanges(
                        calendarIds = state.selectedCalendars,
                        startTime = range.startDate.toStartOfDayMillis(),
                        endTime = range.endDate.toEndOfDayMillis(),
                        initialEvents = events,
                    )

                    if (wasEmpty && days.isNotEmpty()) {
                        sideEffect(AgendaStore.SideEffect.InitialDataLoaded)
                    }
                }
                .onFailure {
                    reduce {
                        copy(
                            isLoading = false,
                        )
                    }
                }
        }
    }

    private fun loadPreviousPage() {
        if (state.isLoadingPrevious || !state.canLoadPrevious) return

        val earliestDate = state.earliestDate ?: return

        val range = loadingStrategy.calculatePreviousRange(earliestDate) ?: return

        reduce {
            copy(isLoadingPrevious = true)
        }

        scope.launch {
            eventsRepository.getEvents(
                calendarIds = state.selectedCalendars,
                startTime = range.startDate.toStartOfDayMillis(),
                endTime = range.endDate.toEndOfDayMillis(),
            ).onSuccess { events ->
                val newDays = groupEventsByDays(
                    events = events,
                    startDate = range.startDate,
                    endDate = range.endDate,
                )

                reduce {
                    copy(
                        days = newDays + days,
                        isLoadingPrevious = false,
                    )
                }

                val newEarliestDate = state.earliestDate ?: return@onSuccess
                val newLatestDate = state.latestDate ?: return@onSuccess
                val allCurrentEvents = state.days.flatMap { it.events }

                startObservingChanges(
                    calendarIds = state.selectedCalendars,
                    startTime = newEarliestDate.toStartOfDayMillis(),
                    endTime = newLatestDate.toEndOfDayMillis(),
                    initialEvents = allCurrentEvents,
                )
            }.onFailure {
                reduce {
                    copy(isLoadingPrevious = false)
                }
            }
        }
    }

    private fun loadNextPage() {
        if (state.isLoadingNext) return
        if (!state.canLoadNext) return

        val latestDate = state.latestDate ?: return

        val range = loadingStrategy.calculateNextRange(latestDate) ?: return

        reduce {
            copy(isLoadingNext = true)
        }

        scope.launch {
            eventsRepository.getEvents(
                calendarIds = state.selectedCalendars,
                startTime = range.startDate.toStartOfDayMillis(),
                endTime = range.endDate.toEndOfDayMillis(),
            ).onSuccess { events ->
                val newDays = groupEventsByDays(
                    events = events,
                    startDate = range.startDate,
                    endDate = range.endDate,
                )

                reduce {
                    copy(
                        days = days + newDays,
                        isLoadingNext = false,
                    )
                }

                val newEarliestDate = state.earliestDate ?: return@onSuccess
                val newLatestDate = state.latestDate ?: return@onSuccess
                val allCurrentEvents = state.days.flatMap { it.events }

                startObservingChanges(
                    calendarIds = state.selectedCalendars,
                    startTime = newEarliestDate.toStartOfDayMillis(),
                    endTime = newLatestDate.toEndOfDayMillis(),
                    initialEvents = allCurrentEvents,
                )
            }.onFailure {
                reduce {
                    copy(isLoadingNext = false)
                }
            }
        }
    }

    private fun processCalendarChanges(changes: List<EventChange>) {
        val currentEventsMap = state.days
            .flatMap { it.events }
            .associateBy { it.id }
            .toMutableMap()

        changes.forEach { change ->
            when (change) {
                is EventChange.Added -> {
                    currentEventsMap[change.event.id] = change.event
                }
                is EventChange.Removed -> {
                    currentEventsMap.remove(change.event.id)
                }
                is EventChange.Modified -> {
                    currentEventsMap[change.event.id] = change.event
                }
            }
        }

        val updatedEvents = currentEventsMap.values.toList()
        val earliestDate = state.earliestDate ?: return
        val latestDate = state.latestDate ?: return

        val updatedDays = groupEventsByDays(
            events = updatedEvents,
            startDate = earliestDate,
            endDate = latestDate,
        )

        reduce {
            copy(days = updatedDays)
        }
    }

    private fun groupEventsByDays(
        events: List<CalendarEvent>,
        startDate: LocalDate,
        endDate: LocalDate,
    ): List<AgendaDay> {
        val timeZone = TimeZone.currentSystemDefault()
        val eventsByDate = events.groupBy { event ->
            Instant
                .fromEpochMilliseconds(event.startTime)
                .toLocalDateTime(timeZone)
                .date
        }

        return buildList {
            var currentDate = startDate
            while (currentDate <= endDate) {
                val dayEvents = eventsByDate[currentDate] ?: emptyList()
                this += AgendaDay(
                    date = currentDate,
                    events = dayEvents,
                )
                currentDate = currentDate.plus(DatePeriod(days = 1))
            }
        }
    }

    private fun LocalDate.toStartOfDayMillis(): Long {
        return atTime(
            LocalTime(
                0,
                0,
                0,
                0,
            ),
        )
            .toInstant(TimeZone.currentSystemDefault())
            .toEpochMilliseconds()
    }

    private fun LocalDate.toEndOfDayMillis(): Long {
        return atTime(LocalTime(23, 59, 59, 999_999_999))
            .toInstant(TimeZone.currentSystemDefault())
            .toEpochMilliseconds()
    }
}