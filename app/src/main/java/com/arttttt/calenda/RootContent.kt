package com.arttttt.calenda

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.arttttt.calenda.feature.addcalendar.presentation.AddCalendarScreen
import com.arttttt.calenda.feature.agenda.AgendaScreen
import com.arttttt.calenda.feature.permissions.presentation.PermissionsScreen
import com.arttttt.calenda.metro.getUIGraph
import com.arttttt.calenda.nav3.BottomSheetSceneStrategy
import com.arttttt.nav3router.Nav3Host

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootContent(
    modifier: Modifier,
) {

    val calendarPermissionsManager = getUIGraph().calendarPermissionsManager
    val router = getUIGraph().router

    val backStack = rememberNavBackStack(
        if (calendarPermissionsManager.canReadCalendar()) {
            Screen.Agenda
        } else {
            Screen.CalendarPermission
        }
    )

    Nav3Host(
        backStack = backStack,
        router = router,
    ) { backStack, onBack, _ ->
        NavDisplay(
            modifier = modifier,
            backStack = backStack,
            entryDecorators = listOf(
                rememberSceneSetupNavEntryDecorator(),
                rememberSavedStateNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            sceneStrategy = BottomSheetSceneStrategy(),
            entryProvider = entryProvider {
                entry<Screen.CalendarPermission> { PermissionsScreen() }
                entry<Screen.Agenda> { AgendaScreen() }
                entry<Screen.AddCalendar>(
                    metadata = BottomSheetSceneStrategy.bottomSheet(
                        modalBottomSheetProperties = ModalBottomSheetDefaults.properties
                    )
                ) { AddCalendarScreen() }
            }
        )
    }
}