package com.arttttt.calenda.metro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.arttttt.calenda.di.UIScope
import com.arttttt.calenda.feature.agenda.AgendaViewModel
import com.arttttt.calenda.feature.agenda.di.AgendaScreenGraph
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider

@ContributesBinding(UIScope::class)
@Inject
class MetroViewModelFactory(
    private val viewModelGraphFactory: ViewModelGraph.Factory,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras,
    ): T {
        val viewModelScope = viewModelGraphFactory.createViewModelGraph(extras)

        return when {
            modelClass.isAssignableFrom(AgendaViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                viewModelScope.agendaViewModel as T
            }
            else -> {
                val factory = viewModelScope.viewModelProviders[modelClass.kotlin] ?: error("Unknown ViewModel class: $modelClass")

                @Suppress("UNCHECKED_CAST")
                factory.invoke() as T
            }
        }
    }
}
