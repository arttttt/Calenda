package com.arttttt.calenda.feature.eventdetails.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.LocalNavEntry
import com.arttttt.calenda.Screen
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

@Composable
fun EventDetailsScreen() {
    val entry = LocalNavEntry.current
    val eventDetails = entry.contentKey as Screen.EventDetails

    EventDetailsContent(
        title = eventDetails.title,
        description = eventDetails.description,
        location = eventDetails.location,
        startTime = eventDetails.startTime,
        endTime = eventDetails.endTime,
        isAllDay = eventDetails.isAllDay,
        color = Color(eventDetails.color),
    )
}

@Composable
private fun EventDetailsContent(
    title: String,
    description: String?,
    location: String?,
    startTime: Long,
    endTime: Long,
    isAllDay: Boolean,
    color: Color,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
    ) {
        // Event color indicator and title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        color = color,
                        shape = CircleShape,
                    ),
            )

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Time section
        DetailSection(
            label = "Time",
            value = formatEventTime(startTime, endTime, isAllDay),
        )

        if (location != null) {
            Spacer(modifier = Modifier.height(16.dp))
            DetailSection(
                label = "Location",
                value = location,
            )
        }

        if (description != null && description.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            DetailSection(
                label = "Description",
                value = description,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DetailSection(
    label: String,
    value: String,
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

private fun formatEventTime(startTime: Long, endTime: Long, isAllDay: Boolean): String {
    if (isAllDay) {
        val timeZone = TimeZone.currentSystemDefault()
        val date = Instant
            .fromEpochMilliseconds(startTime)
            .toLocalDateTime(timeZone)
            .date

        val monthName = MonthNames.ENGLISH_FULL.names[date.month.number - 1]
        return "$monthName ${date.dayOfMonth}, All day"
    }

    val timeZone = TimeZone.currentSystemDefault()
    val startDateTime = Instant
        .fromEpochMilliseconds(startTime)
        .toLocalDateTime(timeZone)
    val endDateTime = Instant
        .fromEpochMilliseconds(endTime)
        .toLocalDateTime(timeZone)

    val startDate = startDateTime.date
    val endDate = endDateTime.date

    val monthName = MonthNames.ENGLISH_FULL.names[startDate.month.number - 1]
    val dateStr = "$monthName ${startDate.dayOfMonth}"
    val timeStr = "${startDateTime.time.formatTime()} - ${endDateTime.time.formatTime()}"

    return if (startDate == endDate) {
        "$dateStr, $timeStr"
    } else {
        val endMonthName = MonthNames.ENGLISH_FULL.names[endDate.month.number - 1]
        "$monthName ${startDate.dayOfMonth} ${startDateTime.time.formatTime()} - $endMonthName ${endDate.dayOfMonth} ${endDateTime.time.formatTime()}"
    }
}

private fun LocalTime.formatTime(): String {
    val hourStr = hour.toString().padStart(2, '0')
    val minuteStr = minute.toString().padStart(2, '0')
    return "$hourStr:$minuteStr"
}

@Preview(showBackground = true)
@Composable
private fun EventDetailsContentPreview() {
    MaterialTheme {
        EventDetailsContent(
            title = "Team Meeting with stakeholders to discuss quarterly goals",
            description = "Discuss Q4 goals, review progress on current projects, and align on priorities for the next quarter.",
            location = "Conference Room A, Building 2",
            startTime = 1696161600000, // Example timestamp
            endTime = 1696167000000,   // Example timestamp
            isAllDay = false,
            color = Color.Blue,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EventDetailsContentAllDayPreview() {
    MaterialTheme {
        EventDetailsContent(
            title = "Company Holiday",
            description = null,
            location = null,
            startTime = 1696118400000,
            endTime = 1696204800000,
            isAllDay = true,
            color = Color.Red,
        )
    }
}
