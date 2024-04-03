package com.github.primodev23.calendar.models

import androidx.compose.runtime.Immutable
import com.github.primodev23.calendar.CalendarState
import java.io.Serializable
import java.time.DayOfWeek
import java.time.LocalDate

@Immutable
data class Month(
    private val date: LocalDate,
    private val startOfWeek: DayOfWeek = DayOfWeek.MONDAY
) : Serializable {

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

    fun plusMonths(monthsToAdd: Long): Month {
        return if (monthsToAdd == 0L) {
            this
        } else {
            Month(
                date = startDate.plusMonths(monthsToAdd),
                startOfWeek = startOfWeek
            )
        }
    }
}
