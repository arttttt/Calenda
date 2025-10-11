package com.arttttt.calenda.feature.agenda.lazylist.item

import com.arttttt.calenda.common.presentation.ListItem

data class AgendaEventItem(
    val id: Long,
    val title: String,
    val time: String,
    val location: String?,
    val color: Int,
    val isAllDay: Boolean,
) : ListItem {

    override val key: Any = id
}