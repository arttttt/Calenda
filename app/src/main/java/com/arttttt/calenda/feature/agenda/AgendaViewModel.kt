package com.arttttt.calenda.feature.agenda

import androidx.lifecycle.ViewModel
import com.arttttt.calenda.Screen
import com.arttttt.calenda.feature.addcalendar.domain.AddCalendarStore
import com.arttttt.calenda.metro.ViewModelKey
import com.arttttt.calenda.metro.ViewModelScope
import com.arttttt.nav3router.Router
import com.arttttt.simplemvi.viewmodel.attachStore
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject

@ViewModelKey(AgendaViewModel::class)
@ContributesIntoMap(ViewModelScope::class)
@Inject
class AgendaViewModel(
    private val router: Router<Screen>,
    private val addCalendarStore: AddCalendarStore,
) : ViewModel() {

    init {
        attachStore(addCalendarStore)
    }

    fun addCalendar() {
        router.push(Screen.AddCalendar)
    }
}