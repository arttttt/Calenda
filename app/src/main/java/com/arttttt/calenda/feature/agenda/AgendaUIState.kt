package com.arttttt.calenda.feature.agenda

import com.arttttt.calenda.common.presentation.ListItem

data class AgendaUIState(
    val items: List<ListItem>,
    val isRefreshing: Boolean,
    val canLoadPrevious: Boolean,
    val canLoadNext: Boolean,
)