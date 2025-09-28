package com.arttttt.calenda.common.domain.repository

import kotlinx.coroutines.flow.Flow

interface SelectedCalendarsRepository {

    fun observeSelectedCalendars(): Flow<Set<Long>>
    suspend fun getSelectedCalendars(): Result<Set<Long>>
    suspend fun saveSelectedCalendars(calendarIds: Set<Long>): Result<Unit>
}