package com.arttttt.calenda.di

import android.content.Context
import com.arttttt.calenda.common.data.SelectedCalendarsRepositoryImpl
import com.arttttt.calenda.common.domain.repository.SelectedCalendarsRepository
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
    fun provideSelectedCalendarsRepository(
        context: Context,
    ): SelectedCalendarsRepository {
        return SelectedCalendarsRepositoryImpl(
            context = context,
        )
    }
}