package com.arttttt.calenda.feature.agenda.lazylist.item

import com.arttttt.calenda.common.presentation.ListItem

data class AgendaEventItem(
    val id: Long,
    val eventId: Long,
    val title: String,
    val description: String?,
    val time: String,
    val location: String?,
    val startTime: Long,
    val endTime: Long,
    val color: Int,
    val isAllDay: Boolean,
) : ListItem {

    override val key: Any = id
}