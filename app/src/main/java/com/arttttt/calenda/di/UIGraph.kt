package com.arttttt.calenda.di

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.arttttt.calenda.Screen
import com.arttttt.calenda.feature.agenda.di.AgendaScreenGraph
import com.arttttt.calenda.feature.permissions.domain.CalendarPermissionsManager
import com.arttttt.nav3router.Router
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@GraphExtension(
    scope = UIScope::class,
)
interface UIGraph {

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

    val agendaScreenGraphFactory: AgendaScreenGraph.Factory

    @SingleIn(UIScope::class)
    @Provides
    private fun provideRouter(): Router<Screen> {
        return Router()
    }
}