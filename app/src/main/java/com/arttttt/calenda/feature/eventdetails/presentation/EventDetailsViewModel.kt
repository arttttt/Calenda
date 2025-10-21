package com.arttttt.calenda.feature.eventdetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arttttt.calenda.feature.eventdetails.domain.EventDetailsStore
import com.arttttt.simplemvi.viewmodel.attachStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class EventDetailsViewModel(
    private val eventDetailsStore: EventDetailsStore,
) : ViewModel() {

    val uiState = eventDetailsStore
        .states
        .map { state -> state.toUIState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = eventDetailsStore.state.toUIState(),
        )

    init {
        attachStore(eventDetailsStore)
    }

    private fun EventDetailsStore.State.toUIState(): EventDetailsUIState {
        val event = this.event

        return if (event != null) {
            EventDetailsUIState(
                title = event.title,
                description = event.description,
                location = event.location,
                startTime = event.startTime,
                endTime = event.endTime,
                isAllDay = event.isAllDay,
                color = event.color,
                isLoading = isLoading,
                error = error,
            )
        } else {
            EventDetailsUIState(
                title = "",
                description = null,
                location = null,
                startTime = 0L,
                endTime = 0L,
                isAllDay = false,
                color = 0,
                isLoading = isLoading,
                error = error,
            )
        }
    }
}
