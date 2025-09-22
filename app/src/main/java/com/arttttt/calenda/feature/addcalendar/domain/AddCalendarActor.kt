package com.arttttt.calenda.feature.addcalendar.domain

import com.arttttt.calenda.common.domain.repository.CalendarRepository
import com.arttttt.simplemvi.actor.DefaultActor
import kotlinx.coroutines.launch

class AddCalendarActor(
    private val calendarRepository: CalendarRepository,
) : DefaultActor<AddCalendarStore.Intent, AddCalendarStore.State, AddCalendarStore.SideEffect>() {

    override fun handleIntent(intent: AddCalendarStore.Intent) {
        when (intent) {
            is AddCalendarStore.Intent.LoadCalendars -> loadCalendars()
            is AddCalendarStore.Intent.ToggleCalendar -> toggleCalendar(intent.id)
        }
    }

    private fun toggleCalendar(id: Long) {
        reduce {
            copy(
                selectedCalendars = if (id in selectedCalendars) {
                    selectedCalendars - id
                } else {
                    selectedCalendars + id
                }
            )
        }
    }

    private fun loadCalendars() {
        if (state.isInProgress) return

        reduce { copy(isInProgress = true) }

        scope.launch {
            calendarRepository
                .getCalendars()
                .onSuccess { calendars ->
                    reduce { copy(calendars = calendars) }
                }

            reduce { copy(isInProgress = false) }
        }
    }
}