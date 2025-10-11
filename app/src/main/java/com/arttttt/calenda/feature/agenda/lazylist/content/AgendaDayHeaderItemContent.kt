package com.arttttt.calenda.feature.agenda.lazylist.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun AgendaDayHeaderItemContent(
    modifier: Modifier,
    dayOfWeek: String,
    dayOfMonth: String,
    month: String,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp,
            ),
    ) {
        Text(
            text = "$dayOfWeek, $month $dayOfMonth",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AgendaDayHeaderItemContentPreview() {
    val today = LocalDate.now()
    AgendaDayHeaderItemContent(
        modifier = Modifier,
        dayOfWeek = today.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()),
        dayOfMonth = today.dayOfMonth.toString(),
        month = today.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
    )
}