package com.arttttt.calenda.feature.agenda.domain.repository

import com.arttttt.calenda.common.domain.model.CalendarEvent

interface EventsRepository {

    suspend fun getEvents(
        calendarIds: Set<Long>,
        startTime: Long,
        endTime: Long,
    ): Result<List<CalendarEvent>>
}