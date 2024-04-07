package com.github.primodev23.calendar.models

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

class MonthTest {

    private val testDate = LocalDate.of(2024, 2, 1)

    @Test
    fun days_hasAllDaysOfMonth() {
        val month = Month(testDate)
        val rangeStart = Day(
            date = LocalDate.of(2024, 1, 29),
            month = month
        )
        val rangeEnd = Day(
            date = LocalDate.of(2024, 3, 3),
            month = month
        )

        val range = rangeStart..rangeEnd

        assertEquals(range.toList(), month.days)
    }

    @Test
    fun days_calculateWithStartOfWeek() {
        DayOfWeek.entries.forEach {
            val month = Month(
                date = testDate,
                startOfWeek = it
            )

            assertEquals(month.days.first().date.dayOfWeek, it)
        }
    }

    @Test
    fun startDate() {
        val month = Month(date = LocalDate.of(2024, 4, 16))

        assertEquals(month.startDate, LocalDate.of(2024, 4, 1))
    }

    @Test
    fun plus() {
        val month = Month(date = LocalDate.of(2024, 3, 1))
        val nextMonth = month + 1

        assertEquals(LocalDate.of(2024, 4, 1), nextMonth.startDate)
    }

    @Test
    fun minus() {
        val month = Month(date = LocalDate.of(2024, 3, 1))
        val nextMonth = month - 1

        assertEquals(LocalDate.of(2024, 2, 1), nextMonth.startDate)
    }

}