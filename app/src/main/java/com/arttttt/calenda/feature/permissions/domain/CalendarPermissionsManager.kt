package com.arttttt.calenda.feature.permissions.domain

interface CalendarPermissionsManager {

    fun canReadCalendar(): Boolean

    suspend fun requestReadCalendarPermission(): Boolean
}