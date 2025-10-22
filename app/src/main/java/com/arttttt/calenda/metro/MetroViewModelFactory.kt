package com.arttttt.calenda.metro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.arttttt.calenda.di.UIScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KClass

@ContributesBinding(UIScope::class)
@Inject
class MetroViewModelFactory(
    private val viewModelProviders: Map<KClass<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>,
        extras: CreationExtras,
    ): T {
        val provider = viewModelProviders[modelClass.kotlin]
            ?: viewModelProviders.entries.firstOrNull { (key, _) -> modelClass.isAssignableFrom(key.java) }
                ?.value
            ?: error("Unknown ViewModel class: $modelClass")

        @Suppress("UNCHECKED_CAST")
        return provider.invoke() as T
    }
}
