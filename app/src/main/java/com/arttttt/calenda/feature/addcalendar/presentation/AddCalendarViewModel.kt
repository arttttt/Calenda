package com.arttttt.calenda.feature.addcalendar.presentation

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arttttt.calenda.feature.addcalendar.domain.AddCalendarStore
import com.arttttt.calenda.feature.addcalendar.presentation.lazylist.item.CalendarListItem
import com.arttttt.calenda.metro.ViewModelKey
import com.arttttt.calenda.metro.ViewModelScope
import com.arttttt.simplemvi.viewmodel.attachStore
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@ViewModelKey(AddCalendarViewModel::class)
@ContributesIntoMap(ViewModelScope::class)
@Inject
class AddCalendarViewModel(
    private val addCalendarStore: AddCalendarStore,
) : ViewModel() {

    val uiState = addCalendarStore
        .states
        .map { state ->
            AddCalendarUIState(
                items = state.calendars.map { calendarInfo ->
                    CalendarListItem(
                        id = calendarInfo.id,
                        title = calendarInfo.displayName,
                        color = Color(calendarInfo.color),
                    )
                },
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AddCalendarUIState(
                items = emptyList(),
            ),
        )

    init {
        attachStore(addCalendarStore)
    }
}