package com.arttttt.calenda.widget

import com.arttttt.calenda.common.domain.model.DateRange
import com.arttttt.calenda.common.domain.strategy.AgendaLoadingStrategy
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

class WidgetAgendaLoadingStrategy : AgendaLoadingStrategy {

    override fun calculateInitialRange(currentDate: LocalDate): DateRange {
        return DateRange(
            startDate = currentDate,
            endDate = currentDate.plus(DatePeriod(days = 1)),
        )
    }

    override fun calculatePreviousRange(earliestDate: LocalDate): DateRange? {
        return null
    }

    override fun calculateNextRange(latestDate: LocalDate): DateRange? {
        return null
    }
}