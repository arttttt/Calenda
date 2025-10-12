package com.arttttt.calenda.common.domain.strategy

import com.arttttt.calenda.common.domain.model.DateRange
import kotlinx.datetime.LocalDate

interface AgendaLoadingStrategy {

    fun calculateInitialRange(currentDate: LocalDate): DateRange

    fun calculatePreviousRange(earliestDate: LocalDate): DateRange?

    fun calculateNextRange(latestDate: LocalDate): DateRange?
}