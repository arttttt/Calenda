package com.arttttt.calenda.feature.agenda

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arttttt.calenda.Screen
import com.arttttt.calenda.arch.CommandsHandler
import com.arttttt.calenda.common.domain.model.AgendaDay
import com.arttttt.calenda.common.domain.model.CalendarEvent
import com.arttttt.calenda.common.domain.store.AgendaStore
import com.arttttt.calenda.common.presentation.ListItem
import com.arttttt.calenda.feature.agenda.lazylist.item.AgendaDayHeaderItem
import com.arttttt.calenda.feature.agenda.lazylist.item.AgendaEventItem
import com.arttttt.calenda.feature.agenda.lazylist.item.AgendaLoadingItem
import com.arttttt.calenda.feature.agenda.lazylist.item.AgendaWeekHeaderItem
import com.arttttt.calenda.feature.agenda.lazylist.item.NoSelectedCalendarsItem
import com.arttttt.calenda.metro.ViewModelKey
import com.arttttt.calenda.metro.ViewModelScope
import com.arttttt.nav3router.Router
import com.arttttt.simplemvi.store.plus
import com.arttttt.simplemvi.viewmodel.attachStore
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.collections.plusAssign
import kotlin.time.Instant

@ViewModelKey(AgendaViewModel::class)
@ContributesIntoMap(ViewModelScope::class)
@Inject
class AgendaViewModel(
    private val agendaStore: AgendaStore,
    private val router: Router<Screen>,
) : ViewModel() {

    sealed interface Command {

        data class ScrollTo(
            val index: Int,
        ) : Command
    }

    val uiState = agendaStore
        .states
        .map { state -> state.toUIState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = agendaStore.state.toUIState(),
        )

    val commands = CommandsHandler<Command>()

    init {
        attachStore(agendaStore)

        agendaStore
            .sideEffects
            .filterIsInstance<AgendaStore.SideEffect.InitialDataLoaded>()
            .onEach {
                val state = agendaStore.state
                val items = uiState.value.items
                val currentDateIndex = items.indexOfFirst { item ->
                    item is AgendaDayHeaderItem && item.date == state.currentDate
                }

                if (currentDateIndex != -1) {
                    commands.sendCommand(Command.ScrollTo(currentDateIndex))
                }
            }
            .launchIn(viewModelScope)
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
                    addWeekGroupedItems(days)
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

    private fun MutableList<ListItem>.addWeekGroupedItems(days: List<AgendaDay>) {
        val weeks = days.groupBy { day -> day.date.getWeekStart() }

        weeks.forEach { (weekStart, daysInWeek) ->
            val hasEvents = daysInWeek.any { it.events.isNotEmpty() }

            if (hasEvents) {
                addWeekWithEvents(daysInWeek)
            } else {
                addEmptyWeek(weekStart)
            }
        }
    }

    private fun MutableList<ListItem>.addWeekWithEvents(daysInWeek: List<AgendaDay>) {
        daysInWeek.forEach { day ->
            if (day.events.isNotEmpty()) {
                this += createDayHeaderItem(day.date)
                day.events.forEach { event ->
                    this += createEventItem(event)
                }
            }
        }
    }

    private fun MutableList<ListItem>.addEmptyWeek(weekStart: LocalDate) {
        val weekEnd = weekStart.plus(DatePeriod(days = 6))
        val weekLabel = formatWeekRange(weekStart, weekEnd)

        this += AgendaWeekHeaderItem(
            startDate = weekStart,
            endDate = weekEnd,
            weekLabel = weekLabel,
        )
    }

    private fun createDayHeaderItem(date: LocalDate): AgendaDayHeaderItem {
        return AgendaDayHeaderItem(
            date = date,
            dayOfWeek = DayOfWeekNames.ENGLISH_FULL.names[date.dayOfWeek.ordinal],
            dayOfMonth = date.dayOfMonth.toString(),
            month = MonthNames.ENGLISH_ABBREVIATED.names[date.monthNumber - 1],
        )
    }

    private fun createEventItem(event: CalendarEvent): AgendaEventItem {
        return AgendaEventItem(
            id = event.id,
            title = event.title,
            time = formatEventTime(event),
            location = event.location,
            color = event.color,
            isAllDay = event.isAllDay,
        )
    }

    private fun formatEventTime(event: CalendarEvent): String {
        if (event.isAllDay) {
            return "All day"
        }

        val timeZone = TimeZone.currentSystemDefault()
        val startTime = Instant
            .fromEpochMilliseconds(event.startTime)
            .toLocalDateTime(timeZone)
            .time
        val endTime = Instant
            .fromEpochMilliseconds(event.endTime)
            .toLocalDateTime(timeZone)
            .time

        return "${startTime.formatTime()} - ${endTime.formatTime()}"
    }

    private fun LocalTime.formatTime(): String {
        val hourStr = hour.toString().padStart(2, '0')
        val minuteStr = minute.toString().padStart(2, '0')
        return "$hourStr:$minuteStr"
    }

    /**
     * Returns the start of the week (Monday) for the given date
     */
    private fun LocalDate.getWeekStart(): LocalDate {
        val dayOfWeek = this.dayOfWeek.isoDayNumber // Monday = 1, Sunday = 7
        val daysToSubtract = dayOfWeek - 1
        return this.minus(DatePeriod(days = daysToSubtract))
    }

    /**
     * Formats a week range as "Oct 21 - Oct 27" or "Oct 28 - Nov 3" (cross-month)
     */
    private fun formatWeekRange(startDate: LocalDate, endDate: LocalDate): String {
        val monthNames = MonthNames.ENGLISH_ABBREVIATED.names
        val startMonth = monthNames[startDate.monthNumber - 1]
        val endMonth = monthNames[endDate.monthNumber - 1]

        return if (startDate.month == endDate.month) {
            "$startMonth ${startDate.dayOfMonth} - ${endDate.dayOfMonth}"
        } else {
            "$startMonth ${startDate.dayOfMonth} - $endMonth ${endDate.dayOfMonth}"
        }
    }
}