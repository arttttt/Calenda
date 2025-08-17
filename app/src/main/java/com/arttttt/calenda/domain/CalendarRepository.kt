package com.arttttt.calenda.domain

interface CalendarRepository {

    suspend fun getCalendars(): Result<List<CalendarInfo>>
}