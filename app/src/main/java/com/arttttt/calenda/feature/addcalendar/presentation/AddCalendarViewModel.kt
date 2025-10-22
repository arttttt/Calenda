package com.arttttt.calenda.feature.addcalendar.presentation

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arttttt.calenda.di.UIScope
import com.arttttt.calenda.feature.addcalendar.domain.AddCalendarStore
import com.arttttt.calenda.feature.addcalendar.presentation.lazylist.item.CalendarListItem
import com.arttttt.calenda.feature.addcalendar.presentation.lazylist.item.NoCalendarsListItem
import com.arttttt.calenda.metro.ViewModelKey
import com.arttttt.simplemvi.store.plus
import com.arttttt.simplemvi.viewmodel.attachStore
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.collections.map

@ViewModelKey(AddCalendarViewModel::class)
@ContributesIntoMap(UIScope::class)
@Inject
class AddCalendarViewModel(
    private val addCalendarStore: AddCalendarStore,
) : ViewModel() {

    val uiState = addCalendarStore
        .states
        .map { state -> state.toUIState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = addCalendarStore.state.toUIState(),
        )

    init {
        attachStore(addCalendarStore)
    }

    fun calendarClicked(id: Long) {
        addCalendarStore + AddCalendarStore.Intent.ToggleCalendar(id)
    }

    /**
     * todo: progress indicator
     */
    private fun AddCalendarStore.State.toUIState(): AddCalendarUIState {
        val items = when {
            !isInProgress && calendars.isEmpty() -> listOf(NoCalendarsListItem)
            else -> calendars.map { calendarInfo ->
                CalendarListItem(
                    id = calendarInfo.id,
                    title = calendarInfo.displayName,
                    color = Color(calendarInfo.color),
                    checked = calendarInfo.id in selectedCalendars,
                )
            }
        }

        return AddCalendarUIState(
            items = items,
        )
    }
}