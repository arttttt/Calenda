package com.arttttt.calenda.feature.agenda.domain.store

import com.arttttt.calenda.common.domain.repository.SelectedCalendarsRepository
import com.arttttt.calenda.feature.agenda.domain.model.AgendaDay
import com.arttttt.calenda.feature.agenda.domain.repository.EventsRepository
import com.arttttt.simplemvi.store.Store
import com.arttttt.simplemvi.store.createStore
import com.arttttt.simplemvi.store.storeName
import dev.zacsweers.metro.Inject
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

@Inject
class AgendaStore(
    eventsRepository: EventsRepository,
    selectedCalendarsRepository: SelectedCalendarsRepository,
) : Store<AgendaStore.Intent, AgendaStore.State, AgendaStore.SideEffect> by createStore(
    name = storeName<AgendaStore>(),
    initialState = State(
        isLoading = false,
        days = emptyList(),
        selectedCalendars = emptySet(),
        currentDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
        isLoadingPrevious = false,
        isLoadingNext = false,
        canLoadPrevious = true,
        canLoadNext = true,
    ),
    actor = AgendaActor(
        eventsRepository = eventsRepository,
        selectedCalendarsRepository = selectedCalendarsRepository,
    ),
) {

    sealed interface Intent {
        data object LoadInitialData : Intent
        data object LoadPreviousPage : Intent
        data object LoadNextPage : Intent
    }

    data class State(
        val isLoading: Boolean,
        val days: List<AgendaDay>,
        val selectedCalendars: Set<Long>,
        val currentDate: LocalDate,
        val isLoadingPrevious: Boolean,
        val isLoadingNext: Boolean,
        val canLoadPrevious: Boolean,
        val canLoadNext: Boolean,
    ) {
        val earliestDate: LocalDate?
            get() = days.firstOrNull()?.date

        val latestDate: LocalDate?
            get() = days.lastOrNull()?.date
    }

    sealed interface SideEffect {

        data object InitialDataLoaded : SideEffect
    }
}