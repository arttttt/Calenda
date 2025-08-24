package com.arttttt.calenda.common.domain.repository

import com.arttttt.calenda.common.domain.model.CalendarInfo

interface CalendarRepository {

    suspend fun getCalendars(): Result<List<CalendarInfo>>
}