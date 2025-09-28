package com.arttttt.calenda.feature.addcalendar.domain

import com.arttttt.calenda.common.domain.repository.CalendarRepository
import com.arttttt.calenda.common.domain.repository.SelectedCalendarsRepository
import com.arttttt.calenda.extensions.zip
import com.arttttt.simplemvi.actor.DefaultActor
import kotlinx.coroutines.launch

class AddCalendarActor(
    private val calendarRepository: CalendarRepository,
    private val selectedCalendarsRepository: SelectedCalendarsRepository,
) : DefaultActor<AddCalendarStore.Intent, AddCalendarStore.State, AddCalendarStore.SideEffect>() {

    override fun onInit() {
        loadData()
    }

    override fun handleIntent(intent: AddCalendarStore.Intent) {
        when (intent) {
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

        scope.launch {
            selectedCalendarsRepository.saveSelectedCalendars(state.selectedCalendars)
        }
    }

    private fun loadData() {
        reduce { copy(isInProgress = true) }

        scope.launch {
            zip(
                value1 = { calendarRepository.getCalendars() },
                value2 = { selectedCalendarsRepository.getSelectedCalendars() },
            )
                .onSuccess { (calendars, selectedCalendars) ->
                    reduce {
                        copy(
                            calendars = calendars,
                            selectedCalendars = selectedCalendars,
                        )
                    }
                }

            reduce { copy(isInProgress = false) }
        }
    }
}