package com.arttttt.calenda.feature.agenda.domain

import com.arttttt.calenda.common.domain.model.DateRange
import com.arttttt.calenda.common.domain.strategy.AgendaLoadingStrategy
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class ScreenAgendaLoadingStrategy(
    private val pageSizeDays: Int = DEFAULT_PAGE_SIZE_DAYS,
) : AgendaLoadingStrategy {

    companion object {
        private const val DEFAULT_PAGE_SIZE_DAYS = 14
    }

    override fun calculateInitialRange(currentDate: LocalDate): DateRange {
        return DateRange(
            startDate = currentDate.minus(DatePeriod(days = pageSizeDays)),
            endDate = currentDate.plus(DatePeriod(days = pageSizeDays)),
        )
    }

    override fun calculatePreviousRange(earliestDate: LocalDate): DateRange {
        val endDate = earliestDate.minus(DatePeriod(days = 1))
        val startDate = endDate.minus(DatePeriod(days = pageSizeDays))

        return DateRange(
            startDate = startDate,
            endDate = endDate,
        )
    }

    override fun calculateNextRange(latestDate: LocalDate): DateRange {
        val startDate = latestDate.plus(DatePeriod(days = 1))
        val endDate = startDate.plus(DatePeriod(days = pageSizeDays))

        return DateRange(
            startDate = startDate,
            endDate = endDate,
        )
    }
}