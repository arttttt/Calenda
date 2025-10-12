package com.arttttt.calenda.metro

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.arttttt.calenda.di.UIScope
import com.arttttt.calenda.feature.agenda.AgendaViewModel
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.Provides
import kotlin.reflect.KClass

@GraphExtension(ViewModelScope::class)
interface ViewModelGraph {

    @ContributesTo(UIScope::class)
    @GraphExtension.Factory
    interface Factory {
        fun createViewModelGraph(
            @Provides creationExtras: CreationExtras,
        ): ViewModelGraph
    }

    @Multibinds
    val viewModelProviders: Map<KClass<out ViewModel>, Provider<ViewModel>>

    val agendaViewModel: AgendaViewModel

    @Provides
    fun provideSavedStateHandle(
        creationExtras: CreationExtras,
    ): SavedStateHandle {
        return creationExtras.createSavedStateHandle()
    }
}