package com.arttttt.calenda.di

import android.content.Context
import com.arttttt.calenda.common.data.EventsRepositoryImpl
import com.arttttt.calenda.common.data.SelectedCalendarsRepositoryImpl
import com.arttttt.calenda.common.domain.repository.EventsRepository
import com.arttttt.calenda.common.domain.repository.SelectedCalendarsRepository
import com.arttttt.calenda.common.domain.store.AgendaStore
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@DependencyGraph(
    scope = AppScope::class,
)
interface AppGraph {

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Provides context: Context,
        ): AppGraph
    }

    @Provides
    @SingleIn(AppScope::class)
    fun provideEventsRepository(
        context: Context,
    ): EventsRepository {
        return EventsRepositoryImpl(
            context = context,
        )
    }

    @Provides
    @SingleIn(AppScope::class)
    fun provideSelectedCalendarsRepository(
        context: Context,
    ): SelectedCalendarsRepository {
        return SelectedCalendarsRepositoryImpl(
            context = context,
        )
    }

    @Provides
    @SingleIn(AppScope::class)
    fun provideAgendaStoreFactory(
        eventsRepository: EventsRepository,
        selectedCalendarsRepository: SelectedCalendarsRepository,
    ): AgendaStore.Factory {
        return AgendaStore.Factory(
            eventsRepository = eventsRepository,
            selectedCalendarsRepository = selectedCalendarsRepository,
        )
    }
}