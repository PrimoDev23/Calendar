package com.github.primodev23.calendar.models

import androidx.compose.runtime.Immutable
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

@Immutable
data class Day(
    val date: LocalDate,
    val month: Month? = null,
    val isSelected: Boolean = false,
) : Comparable<Day> {

    val isInSelectedMonth: Boolean
        get() {
            if (month == null) {
                return false
            }

            return date.month == month.startDate.month &&
                    date.year == month.startDate.year
        }

    override fun compareTo(other: Day): Int {
        return date.compareTo(other.date)
    }

    operator fun plus(daysToAdd: Int): Day {
        val newDate = date.plus(
            value = daysToAdd,
            unit = DateTimeUnit.DAY
        )

        return Day(
            date = newDate,
            month = month
        )
    }

    operator fun minus(daysToSubtract: Int): Day {
        val newDate = date.minus(
            value = daysToSubtract,
            unit = DateTimeUnit.DAY
        )

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
