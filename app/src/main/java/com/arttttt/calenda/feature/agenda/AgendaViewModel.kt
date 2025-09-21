package com.arttttt.calenda.feature.agenda

import androidx.lifecycle.ViewModel
import com.arttttt.calenda.Screen
import com.arttttt.calenda.metro.ViewModelKey
import com.arttttt.calenda.metro.ViewModelScope
import com.arttttt.nav3router.Router
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject

@ViewModelKey(AgendaViewModel::class)
@ContributesIntoMap(ViewModelScope::class)
@Inject
class AgendaViewModel(
    private val router: Router<Screen>,
) : ViewModel() {

    fun addCalendar() {
        router.push(Screen.AddCalendar)
    }
}