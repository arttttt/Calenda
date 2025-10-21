package com.arttttt.calenda.feature.agenda.lazylist.item

import com.arttttt.calenda.common.presentation.ListItem
import kotlinx.datetime.LocalDate

data class AgendaEmptyDayItem(
    val date: LocalDate,
    val message: String,
) : ListItem {
    override val key: Any = "empty_day_$date"
}
