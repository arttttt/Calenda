package com.arttttt.calenda.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.arttttt.calenda.metro.ViewModelGraph
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides

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

    val metroViewModelFactory: ViewModelProvider.Factory
}