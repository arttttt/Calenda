package com.arttttt.calenda.common.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.arttttt.calenda.common.domain.repository.SelectedCalendarsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SelectedCalendarsRepositoryImpl(
    private val context: Context,
) : SelectedCalendarsRepository {

    companion object {
        private val SELECTED_CALENDARS_KEY = stringSetPreferencesKey("selected_calendar_ids")
    }

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "selected_calendars")

    override suspend fun getSelectedCalendars(): Result<Set<Long>> {
        return runCatching {
            context
                .dataStore
                .data
                .first()[SELECTED_CALENDARS_KEY]
                ?.mapNotNull { it.toLongOrNull() }
                ?.toSet()
                ?: emptySet()
        }
    }

    override fun observeSelectedCalendars(): Flow<Set<Long>> {
        return context
            .dataStore
            .data
            .map { preferences ->
                preferences[SELECTED_CALENDARS_KEY]
                    ?.mapNotNull { it.toLongOrNull() }
                    ?.toSet()
                    ?: emptySet()
            }
    }

    override suspend fun saveSelectedCalendars(calendarIds: Set<Long>): Result<Unit> {
        return runCatching {
            context.dataStore.edit { preferences ->
                preferences[SELECTED_CALENDARS_KEY] = calendarIds.map { it.toString() }.toSet()
            }
        }
    }
}