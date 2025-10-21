package com.arttttt.calenda.feature.eventdetails.presentation

data class EventDetailsUIState(
    val title: String,
    val description: String?,
    val location: String?,
    val startTime: Long,
    val endTime: Long,
    val isAllDay: Boolean,
    val color: Int,
    val isLoading: Boolean,
    val error: String?,
)
