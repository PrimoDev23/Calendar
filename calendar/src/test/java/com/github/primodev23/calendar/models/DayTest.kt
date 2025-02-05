package com.github.primodev23.calendar.models

import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DayTest {

    private val startDate = Day(LocalDate(2024, 4, 1))
    private val endDate = Day(LocalDate(2024, 4, 10))

    private val expectedRange = listOf(
        Day(LocalDate(2024, 4, 1)),
        Day(LocalDate(2024, 4, 2)),
        Day(LocalDate(2024, 4, 3)),
        Day(LocalDate(2024, 4, 4)),
        Day(LocalDate(2024, 4, 5)),
        Day(LocalDate(2024, 4, 6)),
        Day(LocalDate(2024, 4, 7)),
        Day(LocalDate(2024, 4, 8)),
        Day(LocalDate(2024, 4, 9)),
        Day(LocalDate(2024, 4, 10)),
    )

    @Test
    fun plus() {
        val addedDate = startDate + 2
        val expected = Day(LocalDate(2024, 4, 3))

        assertEquals(expected, addedDate)
    }

    @Test
    fun minus() {
        val addedDate = endDate - 2
        val expected = Day(LocalDate(2024, 4, 8))

        assertEquals(expected, addedDate)
    }

    @Test
    fun rangeTo() {
        val range = startDate..endDate

        assertEquals(expectedRange, range.toList())
    }

    @Test
    fun rangeUntil() {
        val range = startDate..<endDate

        assertEquals(expectedRange.dropLast(1), range.toList())
    }

    @Test
    fun isInSelectedMonth() {
        val month = Month(date = LocalDate(2024, 4, 1))
        val dayOne = Day(
            date = LocalDate(2024, 4, 13),
            month = month
        )

        assertTrue(dayOne.isInSelectedMonth)

        val dayTwo = Day(
            date = LocalDate(2024, 3, 13),
            month = month
        )

        assertFalse(dayTwo.isInSelectedMonth)
    }
}