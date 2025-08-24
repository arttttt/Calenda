package com.arttttt.calenda

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.arttttt.calenda.feature.agenda.AgendaScreen
import com.arttttt.calenda.feature.permissions.presentation.PermissionsScreen
import com.arttttt.calenda.metro.getUIGraph

@Composable
fun RootContent(
    modifier: Modifier,
) {

    val calendarPermissionsManager = getUIGraph().calendarPermissionsManager

    val backStack = rememberNavBackStack(
        if (calendarPermissionsManager.canReadCalendar()) {
            Screen.Agenda
        } else {
            Screen.CalendarPermission
        }
    )

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Screen.CalendarPermission> {
                PermissionsScreen(
                    openMainScreen = {
                        backStack.set(
                            index = backStack.indices.last,
                            element = Screen.Agenda
                        )
                    }
                )
            }

            entry<Screen.Agenda> {
                AgendaScreen()
            }
        }
    )
}