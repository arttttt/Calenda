package com.arttttt.calenda.feature.addcalendar.presentation.lazylist.item

import com.arttttt.calenda.common.presentation.ListItem

data class CalendarListItem(
    val id: Long,
    val title: String,
) : ListItem {

    override val key: Any by this::id
}