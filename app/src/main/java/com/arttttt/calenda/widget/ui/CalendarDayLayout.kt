package com.arttttt.calenda.widget.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview

@Composable
fun CalendarDayLayout() {

    Row(
        modifier = GlanceModifier.fillMaxWidth(),
    ) {
        CalendaDay(
            day = "M",
            date = "12",
        )

        Spacer(
            modifier = GlanceModifier.padding(
                horizontal = 4.dp,
            ),
        )

        Column {
            CalendarEvent(
                title = "Haircut",
            )

            Spacer(
                modifier = GlanceModifier.padding(
                    vertical = 4.dp,
                ),
            )

            CalendarEvent(
                title = "Nothing scheduled",
            )

            Spacer(
                modifier = GlanceModifier.padding(
                    vertical = 4.dp,
                ),
            )

            CalendarEvent(
                title = "Nothing scheduled",
            )
        }
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
fun CalendarDayLayoutPreview() {
    GlanceTheme {
        CalendarDayLayout()
    }
}