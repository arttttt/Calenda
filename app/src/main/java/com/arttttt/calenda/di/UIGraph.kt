package com.arttttt.calenda.di

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.arttttt.calenda.Screen
import com.arttttt.calenda.common.domain.store.AgendaStore
import com.arttttt.calenda.feature.agenda.di.AgendaScreenGraph
import com.arttttt.calenda.feature.eventdetails.di.EventDetailsScreenGraph
import com.arttttt.calenda.feature.eventdetails.domain.EventDetailsStore
import com.arttttt.calenda.feature.permissions.domain.CalendarPermissionsManager
import com.arttttt.calenda.metro.ViewModelGraph
import com.arttttt.nav3router.Router
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@GraphExtension(
    scope = UIScope::class,
)
interface UIGraph : ViewModelGraph.Factory {

    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun create(
            @Provides activity: ComponentActivity,
        ): UIGraph
    }

    val metroViewModelFactory: ViewModelProvider.Factory

    val calendarPermissionsManager: CalendarPermissionsManager

    val router: Router<Screen>

    @SingleIn(UIScope::class)
    @Provides
    private fun provideRouter(): Router<Screen> {
        return Router()
    }

    @Provides
    fun provideAgendaStore(
        factory: AgendaScreenGraph.Factory
    ): AgendaStore {
        return factory.createAgendaScreenGraph().agendaStore
    }

    fun createEventDetailsStore(
        eventId: Long,
    ): EventDetailsStore {
        return createEventDetailsScreenGraph(eventId).eventDetailsStore
    }

    fun createEventDetailsScreenGraph(eventId: Long): EventDetailsScreenGraph
}