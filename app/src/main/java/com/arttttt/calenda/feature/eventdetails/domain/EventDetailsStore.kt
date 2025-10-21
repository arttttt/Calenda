package com.arttttt.calenda.feature.eventdetails.domain

import com.arttttt.calenda.common.domain.model.CalendarEvent
import com.arttttt.calenda.common.domain.repository.EventsRepository
import com.arttttt.simplemvi.store.Store
import com.arttttt.simplemvi.store.createStore
import com.arttttt.simplemvi.store.storeName
import dev.zacsweers.metro.Inject

class EventDetailsStore(
    eventId: Long,
    eventsRepository: EventsRepository,
) : Store<EventDetailsStore.Intent, EventDetailsStore.State, EventDetailsStore.SideEffect> by createStore(
    name = storeName<EventDetailsStore>(),
    initialState = State(
        eventId = eventId,
        event = null,
        isLoading = false,
        error = null,
    ),
    actor = EventDetailsActor(
        eventsRepository = eventsRepository,
    ),
) {

    @Inject
    class Factory(
        private val eventsRepository: EventsRepository,
    ) {
        fun create(eventId: Long): EventDetailsStore {
            return EventDetailsStore(
                eventId = eventId,
                eventsRepository = eventsRepository,
            )
        }
    }

    sealed interface Intent {
        data object LoadEvent : Intent
    }

    data class State(
        val eventId: Long,
        val event: CalendarEvent?,
        val isLoading: Boolean,
        val error: String?,
    )

    sealed interface SideEffect
}
