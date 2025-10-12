package com.arttttt.calenda.feature.agenda.di

import com.arttttt.calenda.common.domain.store.AgendaStore
import com.arttttt.calenda.common.domain.strategy.AgendaLoadingStrategy
import com.arttttt.calenda.di.UIScope
import com.arttttt.calenda.feature.agenda.domain.ScreenAgendaLoadingStrategy
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@GraphExtension(AgendaScreenScope::class)
interface AgendaScreenGraph {

    @ContributesTo(UIScope::class)
    @GraphExtension.Factory
    interface Factory {
        fun createAgendaScreenGraph(): AgendaScreenGraph
    }

    val agendaStore: AgendaStore

    @Provides
    @SingleIn(AgendaScreenScope::class)
    fun provideAgendaLoadingStrategy(): AgendaLoadingStrategy {
        return ScreenAgendaLoadingStrategy()
    }

    @Provides
    @SingleIn(AgendaScreenScope::class)
    fun provideAgendaStore(
        agendaStoreFactory: AgendaStore.Factory,
        loadingStrategy: AgendaLoadingStrategy,
    ): AgendaStore {
        return agendaStoreFactory.create(loadingStrategy)
    }
}