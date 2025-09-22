package com.arttttt.calenda.feature.addcalendar.presentation

import androidx.compose.foundation.layout.PaddingValues
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
import com.arttttt.calenda.feature.addcalendar.presentation.lazylist.content.NoCalendarsItemContent
import com.arttttt.calenda.feature.addcalendar.presentation.lazylist.item.CalendarListItem
import com.arttttt.calenda.feature.addcalendar.presentation.lazylist.item.NoCalendarsListItem
import com.arttttt.calenda.metro.metroViewModel

@Composable
fun AddCalendarScreen() {
    val viewModel = metroViewModel<AddCalendarViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    AddCalendarScreenContent(
        items = uiState.items,
        onCalendarClick = viewModel::calendarClicked,
    )
}

@Composable
private fun AddCalendarScreenContent(
    items: List<ListItem>,
    onCalendarClick: (Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            vertical = 8.dp,
        )
    ) {
        items(
            items = items,
            key = { it.key },
            contentType = { it::class }
        ) { item ->
            when (item) {
                is CalendarListItem -> CalendarItemContent(
                    modifier = Modifier.fillParentMaxWidth(),
                    title = item.title,
                    color = item.color,
                    checked = item.checked,
                    onClick = {
                        onCalendarClick(item.id)
                    },
                )
                is NoCalendarsListItem -> NoCalendarsItemContent(
                    modifier = Modifier.fillParentMaxWidth(),
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
        onCalendarClick = {},
    )
}