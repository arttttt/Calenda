package com.arttttt.calenda.feature.addcalendar.presentation.lazylist.content

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
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
    checked: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Canvas(
            modifier = Modifier.size(24.dp)
        ) {
            drawCircle(
                color = color,
            )
        }

        Text(
            modifier = Modifier.weight(1f),
            text = title,
            maxLines = 1,
        )

        Checkbox(
            checked = checked,
            onCheckedChange = {
                onClick()
            },
        )
    }
}

@Preview
@Composable
private fun CalendarItemContentPreview() {
    Column {
        CalendarItemContent(
            modifier = Modifier,
            title = "Calendar",
            color = Color.Red,
            checked = false,
            onClick = {},
        )

        CalendarItemContent(
            modifier = Modifier,
            title = "Calendar",
            color = Color.Red,
            checked = true,
            onClick = {},
        )
    }
}