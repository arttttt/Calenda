package com.arttttt.calenda.common.domain

interface CalendarRepository {

    suspend fun getCalendars(): Result<List<CalendarInfo>>
}