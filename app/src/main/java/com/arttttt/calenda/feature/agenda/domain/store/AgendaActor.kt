package com.arttttt.calenda.feature.agenda.domain.store

import com.arttttt.calenda.common.domain.model.CalendarEvent
import com.arttttt.calenda.common.domain.repository.SelectedCalendarsRepository
import com.arttttt.calenda.feature.agenda.domain.model.AgendaDay
import com.arttttt.calenda.feature.agenda.domain.repository.EventsRepository
import com.arttttt.simplemvi.actor.DefaultActor
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
) : DefaultActor<AgendaStore.Intent, AgendaStore.State, AgendaStore.SideEffect>() {

    companion object {
        private const val PAGE_SIZE_DAYS = 14
    }

    override fun onInit() {
        observeSelectedCalendars()
    }

    override fun handleIntent(intent: AgendaStore.Intent) {
        when (intent) {
            AgendaStore.Intent.LoadInitialData -> loadInitialData()
            AgendaStore.Intent.LoadPreviousPage -> loadPreviousPage()
            AgendaStore.Intent.LoadNextPage -> loadNextPage()
            AgendaStore.Intent.Refresh -> refresh()
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

    private fun loadInitialData() {
        if (state.isLoading) return

        reduce {
            copy(
                isLoading = true,
                days = emptyList(),
            )
        }

        scope.launch {
            val startDate = state.currentDate
            val endDate = startDate.plus(DatePeriod(days = PAGE_SIZE_DAYS))

            eventsRepository.getEvents(
                calendarIds = state.selectedCalendars,
                startTime = startDate.toStartOfDayMillis(),
                endTime = endDate.toEndOfDayMillis(),
            ).onSuccess { events ->
                val days = groupEventsByDays(
                    events = events,
                    startDate = startDate,
                    endDate = endDate,
                )

                reduce {
                    copy(
                        days = days,
                        isLoading = false,
                    )
                }
            }.onFailure {
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

        reduce {
            copy(isLoadingPrevious = true)
        }

        scope.launch {
            val endDate = earliestDate.minus(DatePeriod(days = 1))
            val startDate = endDate.minus(DatePeriod(days = PAGE_SIZE_DAYS))

            eventsRepository.getEvents(
                calendarIds = state.selectedCalendars,
                startTime = startDate.toStartOfDayMillis(),
                endTime = endDate.toEndOfDayMillis(),
            ).onSuccess { events ->
                val newDays = groupEventsByDays(
                    events = events,
                    startDate = startDate,
                    endDate = endDate,
                )

                reduce {
                    copy(
                        days = newDays + days,
                        isLoadingPrevious = false,
                    )
                }
            }.onFailure {
                reduce {
                    copy(isLoadingPrevious = false)
                }
            }
        }
    }

    private fun loadNextPage() {
        if (state.isLoadingNext || !state.canLoadNext) return

        val latestDate = state.latestDate ?: return

        reduce {
            copy(isLoadingNext = true)
        }

        scope.launch {
            val startDate = latestDate.plus(DatePeriod(days = 1))
            val endDate = startDate.plus(DatePeriod(days = PAGE_SIZE_DAYS))

            eventsRepository.getEvents(
                calendarIds = state.selectedCalendars,
                startTime = startDate.toStartOfDayMillis(),
                endTime = endDate.toEndOfDayMillis(),
            ).onSuccess { events ->
                val newDays = groupEventsByDays(
                    events = events,
                    startDate = startDate,
                    endDate = endDate,
                )

                reduce {
                    copy(
                        days = days + newDays,
                        isLoadingNext = false,
                    )
                }
            }.onFailure {
                reduce {
                    copy(isLoadingNext = false)
                }
            }
        }
    }

    private fun refresh() {
        if (state.isLoading) return

        val earliestDate = state.earliestDate ?: state.currentDate
        val latestDate = state.latestDate ?: state.currentDate.plus(DatePeriod(days = PAGE_SIZE_DAYS))

        reduce {
            copy(isLoading = true)
        }

        scope.launch {
            eventsRepository.getEvents(
                calendarIds = state.selectedCalendars,
                startTime = earliestDate.toStartOfDayMillis(),
                endTime = latestDate.toEndOfDayMillis(),
            ).onSuccess { events ->
                val days = groupEventsByDays(
                    events = events,
                    startDate = earliestDate,
                    endDate = latestDate,
                )

                reduce {
                    copy(
                        days = days,
                        isLoading = false,
                    )
                }
            }.onFailure {
                reduce {
                    copy(isLoading = false)
                }
            }
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
                0
            )
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