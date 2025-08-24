package com.arttttt.calenda.di

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.arttttt.calenda.feature.permissions.domain.CalendarPermissionsManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

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
}