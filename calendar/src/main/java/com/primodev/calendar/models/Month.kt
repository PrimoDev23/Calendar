package com.primodev.calendar.models

import androidx.compose.runtime.Immutable
import com.primodev.calendar.CalendarState
import java.time.LocalDate

@Suppress("MemberVisibilityCanBePrivate")
@Immutable
data class Month(private val date: LocalDate) {
    val startDate: LocalDate = date.withDayOfMonth(1)

    val days = startDate.getAllDays()

    private fun LocalDate.getAllDays(): List<Day> {
        val startDayOfWeek = startDate.dayOfWeek
        val days = mutableListOf<Day>()

        val additionalStartDays = 1L - startDayOfWeek.value
        for (day in additionalStartDays until 0L) {
            val date = startDate.plusDays(day)

            days.add(
                Day(
                    date = date,
                    isInSelectedMonth = false
                )
            )
        }

        val monthLength = this.lengthOfMonth()

        for (day in 1..monthLength) {
            val date = this.withDayOfMonth(day)

            days.add(
                Day(
                    date = date,
                    isInSelectedMonth = true
                )
            )
        }

        val leftDays = CalendarState.WEEK_LENGTH - (days.size % CalendarState.WEEK_LENGTH)

        // This is seven when there are no days left at the end
        if (leftDays < CalendarState.WEEK_LENGTH) {
            val lastDay = days.last()

            repeat(leftDays) { daysToAdd ->
                days.add(
                    Day(
                        date = lastDay.date.plusDays(daysToAdd + 1L),
                        isInSelectedMonth = false
                    )
                )
            }
        }

        return days
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
