package com.arttttt.calenda.metro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.arttttt.calenda.di.UIScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

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

        val factory = viewModelScope.viewModelProviders[modelClass.kotlin] ?: error("Unknown ViewModel class: $modelClass")

        return factory.invoke() as T
    }
}
