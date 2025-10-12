package com.arttttt.calenda.widget.di

import android.content.Context
import com.arttttt.calenda.common.domain.repository.EventsRepository
import com.arttttt.calenda.common.domain.repository.SelectedCalendarsRepository
import com.arttttt.calenda.common.domain.store.AgendaStore
import com.arttttt.calenda.common.domain.strategy.AgendaLoadingStrategy
import com.arttttt.calenda.widget.WidgetAgendaLoadingStrategy
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@GraphExtension(WidgetScope::class)
interface WidgetGraph {

    @GraphExtension.Factory
    @ContributesTo(AppScope::class)
    fun interface Factory {
        fun create(): WidgetGraph
    }

    val agendaStore: AgendaStore

    @Provides
    @SingleIn(WidgetScope::class)
    fun provideAgendaLoadingStrategy(): AgendaLoadingStrategy {
        return WidgetAgendaLoadingStrategy()
    }

    @Provides
    @SingleIn(WidgetScope::class)
    fun provideAgendaStore(
        eventsRepository: EventsRepository,
        selectedCalendarsRepository: SelectedCalendarsRepository,
        loadingStrategy: AgendaLoadingStrategy,
    ): AgendaStore {
        return AgendaStore(
            eventsRepository = eventsRepository,
            selectedCalendarsRepository = selectedCalendarsRepository,
            loadingStrategy = loadingStrategy,
        )
    }
}