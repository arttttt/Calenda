package com.arttttt.calenda.feature.permissions.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arttttt.calenda.arch.CommandsHandler
import com.arttttt.calenda.feature.permissions.domain.CalendarPermissionsManager
import com.arttttt.calenda.metro.ViewModelKey
import com.arttttt.calenda.metro.ViewModelScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.launch

@ViewModelKey(PermissionsViewModel::class)
@ContributesIntoMap(ViewModelScope::class)
@Inject
class PermissionsViewModel(
    private val calendarPermissionsManager: CalendarPermissionsManager
) : ViewModel() {

    sealed interface Command {

        data object OpenMainScreen : Command
    }

    val commands = CommandsHandler<Command>()

    fun requestPermission() {
        viewModelScope.launch {
            val isGranted = calendarPermissionsManager.requestReadCalendarPermission()

            if (isGranted) {
                commands.sendCommand(Command.OpenMainScreen)
            }
        }
    }
}