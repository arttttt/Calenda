package com.arttttt.calenda.feature.agenda.domain.model

import com.arttttt.calenda.common.domain.model.CalendarEvent

sealed interface EventChange {

    val event: CalendarEvent

    data class Added(override val event: CalendarEvent) : EventChange

    data class Removed(override val event: CalendarEvent) : EventChange

    data class Modified(
        val oldEvent: CalendarEvent,
        override val event: CalendarEvent,
    ) : EventChange
}