package com.arttttt.calenda.di

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arttttt.calenda.Screen
import com.arttttt.calenda.feature.permissions.domain.CalendarPermissionsManager
import com.arttttt.nav3router.Router
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metro.Provider
import kotlin.reflect.KClass

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

    @Multibinds
    val viewModelProviders: Map<KClass<out ViewModel>, Provider<ViewModel>>

    @SingleIn(UIScope::class)
    @Provides
    private fun provideRouter(): Router<Screen> {
        return Router()
    }

}