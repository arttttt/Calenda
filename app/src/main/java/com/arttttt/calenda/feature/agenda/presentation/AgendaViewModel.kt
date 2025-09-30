package com.arttttt.calenda.feature.agenda.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arttttt.calenda.Screen
import com.arttttt.calenda.feature.agenda.domain.store.AgendaStore
import com.arttttt.calenda.feature.agenda.presentation.lazylist.item.AgendaDayHeaderItem
import com.arttttt.calenda.feature.agenda.presentation.lazylist.item.AgendaEventItem
import com.arttttt.calenda.feature.agenda.presentation.lazylist.item.AgendaLoadingItem
import com.arttttt.calenda.feature.agenda.presentation.lazylist.item.NoSelectedCalendarsItem
import com.arttttt.calenda.metro.ViewModelKey
import com.arttttt.calenda.metro.ViewModelScope
import com.arttttt.nav3router.Router
import com.arttttt.simplemvi.store.plus
import com.arttttt.simplemvi.viewmodel.attachStore
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

@ViewModelKey(AgendaViewModel::class)
@ContributesIntoMap(ViewModelScope::class)
@Inject
class AgendaViewModel(
    private val agendaStore: AgendaStore,
    private val router: Router<Screen>,
) : ViewModel() {

    val uiState = agendaStore
        .states
        .map { state -> state.toUIState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = agendaStore.state.toUIState(),
        )

    init {
        attachStore(agendaStore)
    }

    fun onAddCalendarClick() {
        router.push(Screen.AddCalendar)
    }

    fun onLoadPrevious() {
        agendaStore + AgendaStore.Intent.LoadPreviousPage
    }

    fun onLoadNext() {
        agendaStore + AgendaStore.Intent.LoadNextPage
    }

    private fun AgendaStore.State.toUIState(): AgendaUIState {
        val items = buildList {
            when {
                isLoading && days.isEmpty() -> {
                    this += AgendaLoadingItem
                }
                selectedCalendars.isEmpty() -> {
                    this += NoSelectedCalendarsItem
                }
                else -> {
                    days.forEach { day ->
                        this += AgendaDayHeaderItem(
                            date = day.date,
                            dayOfWeek = day.date.dayOfWeek.getDisplayName(),
                            dayOfMonth = day.date.dayOfMonth.toString(),
                            month = day.date.month.getShortName(),
                        )

                        day.events.forEach { event ->
                            this += AgendaEventItem(
                                id = event.id,
                                title = event.title,
                                time = if (event.isAllDay) {
                                    "All day"
                                } else {
                                    val timeZone = TimeZone.currentSystemDefault()
                                    val startTime = Instant
                                        .fromEpochMilliseconds(event.startTime)
                                        .toLocalDateTime(timeZone)
                                        .time
                                    val endTime = Instant.fromEpochMilliseconds(event.endTime)
                                        .toLocalDateTime(timeZone)
                                        .time
                                    "${startTime.format()} - ${endTime.format()}"
                                },
                                location = event.location,
                                color = event.color,
                                isAllDay = event.isAllDay,
                            )
                        }
                    }
                }
            }
        }

        return AgendaUIState(
            items = items,
            isRefreshing = isLoading && days.isNotEmpty(),
            canLoadPrevious = canLoadPrevious && !isLoadingPrevious,
            canLoadNext = canLoadNext && !isLoadingNext,
        )
    }

    private fun DayOfWeek.getDisplayName(): String {
        return when (this) {
            DayOfWeek.MONDAY -> "Monday"
            DayOfWeek.TUESDAY -> "Tuesday"
            DayOfWeek.WEDNESDAY -> "Wednesday"
            DayOfWeek.THURSDAY -> "Thursday"
            DayOfWeek.FRIDAY -> "Friday"
            DayOfWeek.SATURDAY -> "Saturday"
            DayOfWeek.SUNDAY -> "Sunday"
        }
    }

    private fun Month.getShortName(): String {
        return when (this) {
            Month.JANUARY -> "Jan"
            Month.FEBRUARY -> "Feb"
            Month.MARCH -> "Mar"
            Month.APRIL -> "Apr"
            Month.MAY -> "May"
            Month.JUNE -> "Jun"
            Month.JULY -> "Jul"
            Month.AUGUST -> "Aug"
            Month.SEPTEMBER -> "Sep"
            Month.OCTOBER -> "Oct"
            Month.NOVEMBER -> "Nov"
            Month.DECEMBER -> "Dec"
        }
    }

    private fun LocalTime.format(): String {
        val hourStr = hour.toString().padStart(2, '0')
        val minuteStr = minute.toString().padStart(2, '0')
        return "$hourStr:$minuteStr"
    }
}