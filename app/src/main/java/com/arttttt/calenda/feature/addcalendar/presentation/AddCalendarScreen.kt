package com.arttttt.calenda.feature.addcalendar.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arttttt.calenda.common.presentation.ListItem
import com.arttttt.calenda.feature.addcalendar.presentation.lazylist.content.CalendarItemContent
import com.arttttt.calenda.feature.addcalendar.presentation.lazylist.item.CalendarListItem
import com.arttttt.calenda.metro.metroViewModel

@Composable
fun AddCalendarScreen() {
    val viewModel = metroViewModel<AddCalendarViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    AddCalendarScreenContent(
        items = uiState.items,
    )
}

@Composable
private fun AddCalendarScreenContent(
    items: List<ListItem>,
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(
            items = items,
            key = { it.key },
            contentType = { it::class }
        ) { item ->
            when (item) {
                is CalendarListItem -> CalendarItemContent(
                    title = item.title,
                )
            }
        }
    }
}

@Preview
@Composable
private fun AddCalendarScreenContentPreview() {
    AddCalendarScreenContent(
        items = emptyList(),
    )
}