package com.arttttt.calenda.common.domain.model

import kotlinx.datetime.LocalDate

data class AgendaDay(
    val date: LocalDate,
    val events: List<CalendarEvent>,
)