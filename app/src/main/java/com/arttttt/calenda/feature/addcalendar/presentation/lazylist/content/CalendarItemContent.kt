package com.arttttt.calenda.feature.addcalendar.presentation.lazylist.content

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CalendarItemContent(
    title: String,
) {
    Text(title)
}

@Preview
@Composable
private fun CalendarItemContentPreview() {
    CalendarItemContent(
        title = "Calendar",
    )
}