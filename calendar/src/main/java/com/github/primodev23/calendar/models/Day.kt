package com.github.primodev23.calendar.models

import androidx.compose.runtime.Immutable
import java.time.LocalDate

@Immutable
data class Day(
    val date: LocalDate,
    val month: Month? = null,
    val isSelected: Boolean = false
) : Comparable<Day> {

    val isInSelectedMonth: Boolean
        get() {
            if (month == null) {
                return false
            }

            return date.monthValue == month.startDate.monthValue &&
                    date.year == month.startDate.year
        }

    override fun compareTo(other: Day): Int {
        return date.compareTo(other.date)
    }

    operator fun plus(daysToAdd: Long): Day {
        val newDate = date.plusDays(daysToAdd)

        return Day(
            date = newDate,
            month = month
        )
    }

    operator fun minus(daysToSubtract: Long): Day {
        val newDate = date.minusDays(daysToSubtract)

        return Day(
            date = newDate,
            month = month
        )
    }

    operator fun rangeTo(other: Day): DayRange {
        return DayRange(this, other)
    }

    operator fun rangeUntil(other: Day): DayRange {
        return DayRange(this, other - 1)
    }
}
