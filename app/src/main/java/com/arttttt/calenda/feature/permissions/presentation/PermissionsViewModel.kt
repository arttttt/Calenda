package com.arttttt.calenda.feature.permissions.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arttttt.calenda.Screen
import com.arttttt.calenda.arch.CommandsHandler
import com.arttttt.calenda.di.UIScope
import com.arttttt.calenda.feature.permissions.domain.CalendarPermissionsManager
import com.arttttt.calenda.metro.ViewModelKey
import com.arttttt.nav3router.Router
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.launch

@ViewModelKey(PermissionsViewModel::class)
@ContributesIntoMap(UIScope::class)
@Inject
class PermissionsViewModel(
    private val router: Router<Screen>,
    private val calendarPermissionsManager: CalendarPermissionsManager,
) : ViewModel() {

    fun requestPermission() {
        viewModelScope.launch {
            val isGranted = calendarPermissionsManager.requestReadCalendarPermission()

            if (isGranted) {
                router.replaceCurrent(Screen.Agenda)
            }
        }
    }
}