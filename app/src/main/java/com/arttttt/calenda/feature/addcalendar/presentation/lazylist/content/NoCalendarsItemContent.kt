package com.arttttt.calenda.feature.addcalendar.presentation.lazylist.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun NoCalendarsItemContent(
    modifier: Modifier,
) {
    Box(
        modifier = modifier.height(200.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "No calendars found",
        )
    }
}

@Preview
@Composable
private fun NoCalendarsItemContentPreview() {
    NoCalendarsItemContent(
        modifier = Modifier,
    )

}