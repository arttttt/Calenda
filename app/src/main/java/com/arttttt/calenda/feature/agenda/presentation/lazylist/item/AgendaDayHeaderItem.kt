package com.arttttt.calenda.feature.agenda.presentation.lazylist.item

import com.arttttt.calenda.common.presentation.ListItem
import kotlinx.datetime.LocalDate

data class AgendaDayHeaderItem(
    val date: LocalDate,
    val dayOfWeek: String,
    val dayOfMonth: String,
    val month: String,
) : ListItem {
    override val key: Any = "header_$date"
}