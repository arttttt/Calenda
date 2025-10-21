package com.arttttt.calenda.common.domain.repository

import com.arttttt.calenda.common.domain.model.CalendarEvent
import com.arttttt.calenda.common.domain.model.EventChange
import kotlinx.coroutines.flow.Flow

interface EventsRepository {

    suspend fun getEvents(
        calendarIds: Set<Long>,
        startTime: Long,
        endTime: Long,
    ): Result<List<CalendarEvent>>

    suspend fun getEventById(eventId: Long): Result<CalendarEvent?>

    fun observeEventChanges(
        calendarIds: Set<Long>,
        startTime: Long,
        endTime: Long,
        initialEvents: List<CalendarEvent>,
    ): Flow<List<EventChange>>
}