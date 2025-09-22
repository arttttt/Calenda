package com.arttttt.calenda.feature.addcalendar.presentation.lazylist.content

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CalendarItemContent(
    modifier: Modifier,
    title: String,
    color: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            maxLines = 1,
        )

        Spacer(modifier = Modifier.width(8.dp))

        Canvas(
            modifier = Modifier.size(24.dp)
        ) {
            drawCircle(
                color = color,
            )
        }
    }
}

@Preview
@Composable
private fun CalendarItemContentPreview() {
    CalendarItemContent(
        modifier = Modifier,
        title = "Calendar",
        color = Color.Red,
        onClick = {},
    )
}