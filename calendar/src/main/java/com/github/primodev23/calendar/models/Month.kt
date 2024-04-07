package com.github.primodev23.calendar.models

import androidx.compose.runtime.Immutable
import com.github.primodev23.calendar.CalendarState
import java.io.Serializable
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Immutable
data class Month internal constructor(
    private val date: LocalDate,
    private val startOfWeek: DayOfWeek
) : Serializable {

    constructor(date: LocalDate) : this(
        date = date,
        startOfWeek = DayOfWeek.MONDAY
    )

    val startDate: LocalDate = date.withDayOfMonth(1)

    val days = getAllDays()

    private fun getAllDays(): List<Day> {
        val dayOfWeekFirst = startDate.dayOfWeek
        val startOffset =
            (CalendarState.WEEK_LENGTH - startOfWeek.value + dayOfWeekFirst.value) % CalendarState.WEEK_LENGTH
        val rangeStartDate = startDate.plusDays(-startOffset.toLong())
        val start = Day(
            date = rangeStartDate,
            month = this@Month
        )

        val monthLength = startDate.lengthOfMonth()
        val modDaysUntilEnd = (startOffset + monthLength) % CalendarState.WEEK_LENGTH
        val endOffset = if (modDaysUntilEnd == 0) {
            0
        } else {
            CalendarState.WEEK_LENGTH - modDaysUntilEnd
        }
        val fullMonthLength = startOffset + monthLength + endOffset - 1L
        val end = start + fullMonthLength

        val range = start..end

        return range.toList()
    }

    operator fun plus(monthsToAdd: Long): Month {
        return if (monthsToAdd == 0L) {
            this
        } else {
            Month(
                date = startDate.plusMonths(monthsToAdd),
                startOfWeek = startOfWeek
            )
        }
    }

    operator fun minus(monthsToSubtract: Long): Month {
        return if (monthsToSubtract == 0L) {
            this
        } else {
            Month(
                date = startDate.minusMonths(monthsToSubtract),
                startOfWeek = startOfWeek
            )
        }
    }

    fun getMonthsBetween(
        end: Month,
        endInclusive: Boolean = false
    ): Int {
        val duration = ChronoUnit.MONTHS.between(this.startDate, end.startDate).toInt()

        return if (endInclusive) {
            duration + 1
        } else {
            duration
        }
    }
}
