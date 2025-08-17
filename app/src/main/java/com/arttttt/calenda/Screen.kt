package com.arttttt.calenda

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen : NavKey {

    @Serializable
    data object CalendarPermission : Screen

    @Serializable
    data object Agenda : Screen
}