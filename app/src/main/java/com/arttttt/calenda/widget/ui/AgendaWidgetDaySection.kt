package com.arttttt.calenda.widget.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.arttttt.calenda.common.domain.model.CalendarEvent
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
fun AgendaWidgetDaySection(
    dayLabel: String,
    date: LocalDate,
    events: List<CalendarEvent>,
) {
    Column(
        modifier = GlanceModifier.fillMaxWidth(),
    ) {
        Text(
            text = dayLabel,
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            ),
        )

        Spacer(modifier = GlanceModifier.height(8.dp))

        if (events.isEmpty()) {
            AgendaWidgetPlaceholder()
        } else {
            events.forEachIndexed { index, event ->
                AgendaWidgetEventItem(event)

                if (index < events.lastIndex) {
                    Spacer(modifier = GlanceModifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun AgendaWidgetPlaceholder() {
    Text(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        text = "Nothing planned",
        style = TextStyle(
            color = GlanceTheme.colors.onSurfaceVariant,
            fontSize = 12.sp,
        ),
    )
}

@Composable
private fun AgendaWidgetEventItem(event: CalendarEvent) {
    Column(
        modifier = GlanceModifier.fillMaxWidth(),
    ) {
        Text(
            text = event.title,
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontSize = 12.sp,
            ),
            maxLines = 1,
        )

        if (!event.isAllDay) {
            val timeZone = TimeZone.currentSystemDefault()
            val startTime = Instant
                .fromEpochMilliseconds(event.startTime)
                .toLocalDateTime(timeZone)
                .time
            val endTime = Instant
                .fromEpochMilliseconds(event.endTime)
                .toLocalDateTime(timeZone)
                .time

            Text(
                text = "${formatTime(startTime.hour, startTime.minute)} - ${formatTime(endTime.hour, endTime.minute)}",
                style = TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontSize = 10.sp,
                ),
            )
        } else {
            Text(
                text = "All day",
                style = TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontSize = 10.sp,
                ),
            )
        }
    }
}

private fun formatTime(hour: Int, minute: Int): String {
    return "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun AgendaWidgetDaySectionPreview() {
    GlanceTheme {
        Column {
            AgendaWidgetDaySection(
                dayLabel = "Today",
                date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                events = emptyList(),
            )

            Spacer(modifier = GlanceModifier.height(16.dp))

            AgendaWidgetDaySection(
                dayLabel = "Tomorrow",
                date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
                events = listOf(
                    CalendarEvent(
                        id = 1,
                        eventId = 1,
                        calendarId = 1,
                        title = "Team Meeting",
                        description = null,
                        location = null,
                        startTime = 1700000000000,
                        endTime = 1700003600000,
                        isAllDay = false,
                        color = 0,
                    ),
                    CalendarEvent(
                        id = 2,
                        eventId = 2,
                        calendarId = 1,
                        title = "Birthday Party",
                        description = null,
                        location = null,
                        startTime = 1700000000000,
                        endTime = 1700003600000,
                        isAllDay = true,
                        color = 0,
                    ),
                ),
            )
        }
    }
}