package com.arttttt.calenda.feature.agenda.domain.model

import com.arttttt.calenda.common.domain.model.CalendarEvent
import kotlinx.datetime.LocalDate

data class AgendaDay(
    val date: LocalDate,
    val events: List<CalendarEvent>,
)