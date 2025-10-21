package com.arttttt.calenda

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen : NavKey {

    @Serializable
    data object CalendarPermission : Screen

    @Serializable
    data object Agenda : Screen

    @Serializable
    data object AddCalendar : Screen

    @Serializable
    data class EventDetails(
        val eventId: Long,
    ) : Screen
}