package com.github.primodev23.calendar.models

import kotlinx.datetime.LocalDate
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SelectionTest {

    @Test
    fun initialSelection() {
        val days = listOf(
            Day(date = LocalDate(2024, 4, 4)),
            Day(date = LocalDate(2024, 4, 7))
        )
        val selection = Selection(days)

        days.forEach {
            assertTrue(selection.contains(it))
        }
    }

    @Test
    fun contains() {
        val day = Day(date = LocalDate(2024, 4, 7))
        val selection = Selection()

        selection.selectedDays.add(day)

        assertTrue(selection.selectedDays.contains(day))

        selection.selectedDays.remove(day)

        assertFalse(selection.contains(day))
    }

    @Test
    fun add() {
        val selection = Selection()

        val day = Day(date = LocalDate(2024, 4, 7))

        selection.add(day)

        assertTrue(selection.selectedDays.contains(day))
    }

    @Test
    fun remove() {
        val selection = Selection()

        val day = Day(date = LocalDate(2024, 4, 7))

        selection.selectedDays.add(day)
        selection.remove(day)

        assertFalse(selection.selectedDays.contains(day))
    }

    @Test
    fun addAll() {
        val selection = Selection()

        val days = listOf(
            Day(date = LocalDate(2024, 4, 4)),
            Day(date = LocalDate(2024, 4, 7))
        )

        selection.addAll(days)

        days.forEach {
            assertTrue(selection.selectedDays.contains(it))
        }
    }

    @Test
    fun clear() {
        val selection = Selection()

        val days = listOf(
            Day(date = LocalDate(2024, 4, 4)),
            Day(date = LocalDate(2024, 4, 7))
        )

        selection.selectedDays.addAll(days)
        selection.clear()

        assertTrue(selection.selectedDays.isEmpty())
    }
}