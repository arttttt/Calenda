package com.arttttt.calenda.common.data

import android.content.Context
import android.database.Cursor
import android.provider.CalendarContract
import com.arttttt.calenda.common.domain.CalendarInfo
import com.arttttt.calenda.common.domain.CalendarRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@ContributesBinding(AppScope::class)
@Inject
class CalendarRepositoryImpl(
    context: Context,
) : CalendarRepository {

    private val contentResolver = context.contentResolver

    private val projection = arrayOf(
        CalendarContract.Calendars._ID,
        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
        CalendarContract.Calendars.ACCOUNT_NAME,
        CalendarContract.Calendars.OWNER_ACCOUNT,
        CalendarContract.Calendars.CALENDAR_COLOR
    )

    private val uri = CalendarContract.Calendars.CONTENT_URI

    override suspend fun getCalendars(): Result<List<CalendarInfo>> {
        return runCatching {
            withContext(Dispatchers.IO) {
                val cursor = getCalendarsCursor()

                buildList {
                    cursor?.use {
                        val idColumn = it.getColumnIndexOrThrow(CalendarContract.Calendars._ID)
                        val displayNameColumn = it.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)
                        val accountNameColumn = it.getColumnIndexOrThrow(CalendarContract.Calendars.ACCOUNT_NAME)
                        val ownerAccountColumn = it.getColumnIndexOrThrow(CalendarContract.Calendars.OWNER_ACCOUNT)
                        val colorColumn = it.getColumnIndexOrThrow(CalendarContract.Calendars.CALENDAR_COLOR)

                        while (it.moveToNext()) {
                            this += it.getCalendarInfo(
                                idColumn = idColumn,
                                displayNameColumn = displayNameColumn,
                                accountNameColumn = accountNameColumn,
                                ownerAccountColumn = ownerAccountColumn,
                                colorColumn = colorColumn,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun Cursor.getCalendarInfo(
        idColumn: Int,
        displayNameColumn: Int,
        accountNameColumn: Int,
        ownerAccountColumn: Int,
        colorColumn: Int,
    ): CalendarInfo {
        return CalendarInfo(
            id = this.getLong(idColumn),
            displayName = this.getString(displayNameColumn) ?: "N/A",
            accountName = this.getString(accountNameColumn) ?: "N/A",
            ownerName = this.getString(ownerAccountColumn) ?: "N/A",
            color = this.getInt(colorColumn),
        )
    }

    private fun getCalendarsCursor(): Cursor? {
        return contentResolver.query(
            uri,
            projection,
            null,
            null,
            null,
        )
    }
}