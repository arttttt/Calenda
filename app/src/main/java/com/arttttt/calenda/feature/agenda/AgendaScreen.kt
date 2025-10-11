package com.arttttt.calenda.feature.agenda

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.arttttt.calenda.common.presentation.ListItem
import com.arttttt.calenda.feature.agenda.lazylist.content.AgendaDayHeaderItemContent
import com.arttttt.calenda.feature.agenda.lazylist.content.AgendaEventItemContent
import com.arttttt.calenda.feature.agenda.lazylist.content.AgendaLoadingItemContent
import com.arttttt.calenda.feature.agenda.lazylist.content.NoSelectedCalendarsItemContent
import com.arttttt.calenda.feature.agenda.lazylist.item.AgendaDayHeaderItem
import com.arttttt.calenda.feature.agenda.lazylist.item.AgendaEventItem
import com.arttttt.calenda.feature.agenda.lazylist.item.AgendaLoadingItem
import com.arttttt.calenda.feature.agenda.lazylist.item.NoSelectedCalendarsItem
import com.arttttt.calenda.metro.metroViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

@Composable
fun AgendaScreen() {
    val viewModel = metroViewModel<AgendaViewModel>()

    val uiState by viewModel.uiState.collectAsState()

    val lazyListState = rememberLazyListState()

    AgendaScreenContent(
        uiState = uiState,
        lazyListState = lazyListState,
        onAddCalendarClick = viewModel::onAddCalendarClick,
    )

    LaunchedEffect(Unit) {
        viewModel
            .commands
            .filterIsInstance<AgendaViewModel.Command.ScrollTo>()
            .onEach { command ->
                lazyListState.requestScrollToItem(command.index)
            }
            .launchIn(this)
    }

    LaunchedEffect(Unit) {
        snapshotFlow {
            lazyListState.firstVisibleItemIndex
        }
            .distinctUntilChanged()
            .filter { firstVisibleItemIndex -> firstVisibleItemIndex <= 3 }
            .collect {
                viewModel.onLoadPrevious()
            }
    }

    LaunchedEffect(Unit) {
        snapshotFlow {
            lazyListState.layoutInfo
        }
            .map { layoutInfo ->
                (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1) to layoutInfo.totalItemsCount
            }
            .distinctUntilChanged()
            .filter { (lastVisibleItemIndex, totalItemsCount) ->
                val totalItems = totalItemsCount
                lastVisibleItemIndex >= totalItems - 3
            }
            .collect {
                viewModel.onLoadNext()
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgendaScreenContent(
    uiState: AgendaUIState,
    lazyListState: LazyListState,
    onAddCalendarClick: () -> Unit,
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Agenda")
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCalendarClick,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add calendar",
                )
            }
        },
    ) { paddingValues ->
        AgendaList(
            modifier = Modifier.padding(paddingValues),
            lazyListState = lazyListState,
            items = uiState.items,
        )
    }
}

@Composable
private fun AgendaList(
    modifier: Modifier,
    lazyListState: LazyListState,
    items: List<ListItem>,
) {
    LazyColumn(
        state = lazyListState,
        modifier = modifier.fillMaxWidth(),
    ) {
        items(
            items = items,
            key = { it.key },
            contentType = { it::class },
        ) { item ->
            when (item) {
                is AgendaDayHeaderItem -> AgendaDayHeaderItemContent(
                    modifier = Modifier
                        .animateItem()
                        .fillParentMaxWidth(),
                    dayOfWeek = item.dayOfWeek,
                    dayOfMonth = item.dayOfMonth,
                    month = item.month,
                )
                is AgendaEventItem -> AgendaEventItemContent(
                    modifier = Modifier
                        .animateItem()
                        .fillParentMaxWidth(),
                    title = item.title,
                    time = item.time,
                    location = item.location,
                    color = Color(item.color),
                    isAllDay = item.isAllDay,
                )
                is AgendaLoadingItem -> AgendaLoadingItemContent(
                    modifier = Modifier
                        .animateItem()
                        .fillParentMaxSize(),
                )
                is NoSelectedCalendarsItem -> NoSelectedCalendarsItemContent(
                    modifier = Modifier
                        .animateItem()
                        .fillParentMaxSize(),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AgendaScreenContentPreview() {
    AgendaScreenContent(
        uiState = AgendaUIState(
            items = listOf(
                AgendaDayHeaderItem(
                    date = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                    dayOfWeek = "Monday",
                    dayOfMonth = "29",
                    month = "Sep",
                ),
                AgendaEventItem(
                    id = 1,
                    title = "Team Meeting",
                    time = "14:00 - 15:00",
                    location = "Room 101",
                    color = 0xFF0000FF.toInt(),
                    isAllDay = false,
                ),
                AgendaEventItem(
                    id = 2,
                    title = "Lunch with client",
                    time = "12:00 - 13:00",
                    location = null,
                    color = 0xFFFF0000.toInt(),
                    isAllDay = false,
                ),
            ),
            isRefreshing = false,
            canLoadPrevious = true,
            canLoadNext = true,
        ),
        lazyListState = rememberLazyListState(),
        onAddCalendarClick = {},
    )
}