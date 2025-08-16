package com.arttttt.calenda.widget.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

@Composable
fun CalendaDay(
    day: String,
    date: String,
) {

    Column(
        modifier = GlanceModifier
            .background(GlanceTheme.colors.onBackground)
            .cornerRadius(8.dp)
            .padding(
                vertical = 2.dp,
                horizontal = 8.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = day,
            style = TextStyle(
                color = GlanceTheme.colors.background,
                fontSize = 12.sp,
            ),
        )

        Text(
            text = date,
            style = TextStyle(
                color = GlanceTheme.colors.background,
                fontSize = 12.sp,
            ),
        )
    }
}

@OptIn(ExperimentalGlancePreviewApi::class)
@Preview
@Composable
private fun CalendaDayPreview() {
    GlanceTheme {
        CalendaDay(
            day = "M",
            date = "12",
        )
    }
}