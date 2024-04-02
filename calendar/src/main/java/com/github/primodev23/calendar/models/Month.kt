package com.github.primodev23.calendar.models

import androidx.compose.runtime.Immutable
import com.github.primodev23.calendar.CalendarState
import java.io.Serializable
import java.time.LocalDate

@Immutable
data class Month(private val date: LocalDate) : Serializable {
    val startDate: LocalDate = date.withDayOfMonth(1)

    val days = getAllDays()

    private fun getAllDays(): List<Day> {
        val startDayOfWeek = startDate.dayOfWeek

        val rangeStartDate = startDate.plusDays(1L - startDayOfWeek.value)

        val monthLength = startDate.lengthOfMonth()
        val endOfMonth = startDate.withDayOfMonth(monthLength)
        val leftDaysUntilSunday = CalendarState.WEEK_LENGTH - endOfMonth.dayOfWeek.value
        val rangeEndDate = endOfMonth.plusDays(leftDaysUntilSunday.toLong())

        val start = Day(
            date = rangeStartDate,
            month = this@Month
        )
        val end = Day(
            date = rangeEndDate,
            month = this@Month
        )
        val range = start..end

        return range.toList()
    }

    fun plusMonths(monthsToAdd: Long): Month {
        return if (monthsToAdd == 0L) {
            this
        } else {
            Month(
                date = startDate.plusMonths(monthsToAdd)
            )
        }
    }
}
