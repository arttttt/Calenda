package com.arttttt.calenda.feature.eventdetails.domain

import com.arttttt.calenda.common.domain.repository.EventsRepository
import com.arttttt.simplemvi.actor.DefaultActor
import kotlinx.coroutines.launch

class EventDetailsActor(
    private val eventsRepository: EventsRepository,
) : DefaultActor<EventDetailsStore.Intent, EventDetailsStore.State, EventDetailsStore.SideEffect>() {

    override fun onInit() {
        intent(EventDetailsStore.Intent.LoadEvent)
    }

    override fun handleIntent(intent: EventDetailsStore.Intent) {
        when (intent) {
            is EventDetailsStore.Intent.LoadEvent -> loadEvent()
        }
    }

    private fun loadEvent() {
        if (state.isLoading) return

        reduce {
            copy(
                isLoading = true,
                error = null,
            )
        }

        scope.launch {
            eventsRepository
                .getEventById(state.eventId)
                .onSuccess { event ->
                    reduce {
                        copy(
                            event = event,
                            isLoading = false,
                            error = if (event == null) "Event not found" else null,
                        )
                    }
                }
                .onFailure { throwable ->
                    reduce {
                        copy(
                            isLoading = false,
                            error = throwable.message ?: "Failed to load event",
                        )
                    }
                }
        }
    }
}
