package com.arttttt.calenda.feature.agenda.lazylist.item

import com.arttttt.calenda.common.presentation.ListItem
import kotlinx.datetime.LocalDate

data class AgendaWeekHeaderItem(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val weekLabel: String,
) : ListItem {
    override val key: Any = "week_header_${startDate}_$endDate"
}
