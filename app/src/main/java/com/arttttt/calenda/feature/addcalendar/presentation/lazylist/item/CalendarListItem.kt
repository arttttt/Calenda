package com.arttttt.calenda.feature.addcalendar.presentation.lazylist.item

import androidx.compose.ui.graphics.Color
import com.arttttt.calenda.common.presentation.ListItem

data class CalendarListItem(
    val id: Long,
    val title: String,
    val color: Color,
    val checked: Boolean,
) : ListItem {

    override val key: Any by this::id
}