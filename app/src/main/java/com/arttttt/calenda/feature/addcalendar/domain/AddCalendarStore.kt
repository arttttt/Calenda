package com.arttttt.calenda.feature.addcalendar.domain

import com.arttttt.calenda.common.domain.model.CalendarInfo
import com.arttttt.calenda.common.domain.repository.CalendarRepository
import com.arttttt.simplemvi.store.Store
import com.arttttt.simplemvi.store.createStore
import com.arttttt.simplemvi.store.storeName
import dev.zacsweers.metro.Inject

@Inject
class AddCalendarStore(
    calendarRepository: CalendarRepository
) : Store<AddCalendarStore.Intent, AddCalendarStore.State, AddCalendarStore.SideEffect> by createStore(
    name = storeName<AddCalendarStore>(),
    initialState = State(
        isInProgress = false,
        calendars = emptyList(),
        selectedCalendars = emptySet(),
    ),
    actor = AddCalendarActor(
        calendarRepository = calendarRepository,
    ),
) {

    sealed interface Intent {
        data class ToggleCalendar(val id: Long) : Intent
    }

    data class State(
        val isInProgress: Boolean,
        val calendars: List<CalendarInfo>,
        val selectedCalendars: Set<Long>,
    )

    sealed interface SideEffect
}