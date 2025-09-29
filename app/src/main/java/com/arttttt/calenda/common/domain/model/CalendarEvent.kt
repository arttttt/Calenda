package com.arttttt.calenda.common.domain.model

data class CalendarEvent(
    val id: Long,
    val eventId: Long,
    val calendarId: Long,
    val title: String,
    val description: String?,
    val location: String?,
    val startTime: Long,
    val endTime: Long,
    val isAllDay: Boolean,
    val color: Int,
)