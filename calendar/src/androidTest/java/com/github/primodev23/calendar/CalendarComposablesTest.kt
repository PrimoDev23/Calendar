package com.github.primodev23.calendar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import androidx.compose.ui.text.TextLayoutResult
import com.github.primodev23.calendar.models.Day
import com.github.primodev23.calendar.models.Month
import com.github.primodev23.calendar.models.Selection
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

class CalendarComposablesTest : BaseCalendarTest() {

    private val startMonth = LocalDate.of(2024, 3, 1)

    @Test
    fun animateScrollToNextMonth() {
        val startDayOfWeek = DayOfWeek.MONDAY
        val startMonth = Month(
            date = startMonth,
            startOfWeek = startDayOfWeek
        )

        createCalendar(
            modifier = Modifier.fillMaxWidth(),
            initialMonth = startMonth,
            startOfWeek = startDayOfWeek
        )

        val nextMonth = state.months[2]

        assertEquals(startMonth, state.settledMonth)
        assertEquals(startMonth, state.targetMonth)

        rule.mainClock.autoAdvance = false

        rule.runOnUiThread {
            scope.launch {
                state.animateScrollToNextMonth()
                delay(1_000L)
            }
        }

        rule.mainClock.advanceTimeByFrame()

        assertEquals(startMonth, state.settledMonth)
        assertEquals(nextMonth, state.targetMonth)

        rule.mainClock.advanceTimeBy(2_000L)

        assertEquals(nextMonth, state.settledMonth)
        assertEquals(nextMonth, state.targetMonth)
    }

    @Test
    fun animateScrollToPreviousMonth() {
        val startDayOfWeek = DayOfWeek.MONDAY
        val startMonth = Month(
            date = startMonth,
            startOfWeek = startDayOfWeek
        )

        createCalendar(
            modifier = Modifier.fillMaxWidth(),
            initialMonth = startMonth,
            startOfWeek = startDayOfWeek
        )

        val previousMonth = state.months[0]

        assertEquals(startMonth, state.settledMonth)
        assertEquals(startMonth, state.targetMonth)

        rule.mainClock.autoAdvance = false

        rule.runOnUiThread {
            scope.launch {
                state.animateScrollToPreviousMonth()
                delay(1_000L)
            }
        }

        rule.mainClock.advanceTimeByFrame()

        assertEquals(startMonth, state.settledMonth)
        assertEquals(previousMonth, state.targetMonth)

        rule.mainClock.advanceTimeBy(2_000L)

        assertEquals(previousMonth, state.settledMonth)
        assertEquals(previousMonth, state.targetMonth)
    }

    @Test
    fun scrollToMonth() {
        val startDayOfWeek = DayOfWeek.MONDAY
        val startMonth = Month(
            date = startMonth,
            startOfWeek = startDayOfWeek
        )
        val newMonth = Month(date = LocalDate.of(2025, 12, 1))

        createCalendar(
            modifier = Modifier.fillMaxWidth(),
            initialMonth = startMonth,
            startOfWeek = startDayOfWeek
        )

        assertEquals(startMonth, state.settledMonth)
        assertEquals(startMonth, state.targetMonth)

        rule.runOnUiThread {
            scope.launch {
                state.scrollToMonth(newMonth)
            }
        }

        assertEquals(newMonth, state.settledMonth)
        assertEquals(newMonth, state.targetMonth)
    }

    @Test
    fun updateStartOfWeek() {
        val startDayOfWeek = DayOfWeek.MONDAY
        val startMonth = Month(
            date = startMonth,
            startOfWeek = startDayOfWeek
        )

        createCalendar(
            modifier = Modifier.fillMaxWidth(),
            initialMonth = startMonth,
            startOfWeek = startDayOfWeek
        )

        assertEquals(startDayOfWeek, state.startOfWeek)

        val newStartDayOfWeek = DayOfWeek.WEDNESDAY

        rule.runOnUiThread {
            scope.launch {
                state.updateStartOfWeek(newStartDayOfWeek)
            }
        }

        val newMonth = Month(
            date = this.startMonth,
            startOfWeek = newStartDayOfWeek
        )

        assertEquals(newMonth, state.settledMonth)
        assertEquals(newMonth, state.targetMonth)
    }

    @Test
    fun selection_initialSelection_notEmpty() {
        val startDayOfWeek = DayOfWeek.MONDAY
        val startMonth = Month(
            date = startMonth,
            startOfWeek = startDayOfWeek
        )

        val selectedDay = Day(
            date = this.startMonth.plusDays(5),
            month = startMonth
        )

        createCalendar(
            modifier = Modifier.fillMaxWidth(),
            initialMonth = startMonth,
            startOfWeek = startDayOfWeek,
            initialSelection = Selection(listOf(selectedDay))
        )

        val node = rule.onNodeWithText("6")
        val initialSelectionResult = node.getTextLayoutResult()

        assertEquals(Color.Red, initialSelectionResult.layoutInput.style.color)
    }

    @Test
    fun selection_initialSelection_empty() {
        val startDayOfWeek = DayOfWeek.MONDAY
        val startMonth = Month(
            date = startMonth,
            startOfWeek = startDayOfWeek
        )

        createCalendar(
            modifier = Modifier.fillMaxWidth(),
            initialMonth = startMonth,
            startOfWeek = startDayOfWeek
        )

        val node = rule.onNodeWithText("6")
        val initialSelectionResult = node.getTextLayoutResult()

        assertEquals(colorScheme.onSurface, initialSelectionResult.layoutInput.style.color)
    }

    @Test
    fun selection_adding_updatesUI() {
        val startDayOfWeek = DayOfWeek.MONDAY
        val startMonth = Month(
            date = startMonth,
            startOfWeek = startDayOfWeek
        )

        createCalendar(
            modifier = Modifier.fillMaxWidth(),
            initialMonth = startMonth,
            startOfWeek = startDayOfWeek
        )

        val selectedDay = Day(
            date = this.startMonth.plusDays(5),
            month = startMonth
        )

        val node = rule.onNodeWithText("6")
        val noSelectionsResult = node.getTextLayoutResult()

        assertEquals(colorScheme.onSurface, noSelectionsResult.layoutInput.style.color)

        state.selection.add(selectedDay)

        val selectedResult = node.getTextLayoutResult()

        assertEquals(Color.Red, selectedResult.layoutInput.style.color)
    }

    @Test
    fun selection_removing_updatesUI() {
        val startDayOfWeek = DayOfWeek.MONDAY
        val startMonth = Month(
            date = startMonth,
            startOfWeek = startDayOfWeek
        )

        createCalendar(
            modifier = Modifier.fillMaxWidth(),
            initialMonth = startMonth,
            startOfWeek = startDayOfWeek
        )

        val selectedDay = Day(
            date = this.startMonth.plusDays(5),
            month = startMonth
        )

        state.selection.add(selectedDay)
        state.selection.remove(selectedDay)

        val node = rule.onNodeWithText("6")
        val removedResult = node.getTextLayoutResult()

        assertEquals(colorScheme.onSurface, removedResult.layoutInput.style.color)
    }

    @Test
    fun selection_addAll_updatesUI() {
        val startDayOfWeek = DayOfWeek.MONDAY
        val startMonth = Month(
            date = startMonth,
            startOfWeek = startDayOfWeek
        )

        createCalendar(
            modifier = Modifier.fillMaxWidth(),
            initialMonth = startMonth,
            startOfWeek = startDayOfWeek
        )

        val selectedDays = listOf(
            Day(
                date = this.startMonth.plusDays(5),
                month = startMonth
            ),
            Day(
                date = this.startMonth.plusDays(9),
                month = startMonth
            )
        )

        state.selection.addAll(selectedDays)

        val nodeSix = rule.onNodeWithText("6")
        val nodeTen = rule.onNodeWithText("10")

        val resultSix = nodeSix.getTextLayoutResult()
        val resultTen = nodeTen.getTextLayoutResult()

        assertEquals(Color.Red, resultSix.layoutInput.style.color)
        assertEquals(Color.Red, resultTen.layoutInput.style.color)
    }

    @Test
    fun selection_clear_updatesUI() {
        val startDayOfWeek = DayOfWeek.MONDAY
        val startMonth = Month(
            date = startMonth,
            startOfWeek = startDayOfWeek
        )

        createCalendar(
            modifier = Modifier.fillMaxWidth(),
            initialMonth = startMonth,
            startOfWeek = startDayOfWeek,
            initialSelection = Selection(
                listOf(
                    Day(
                        date = this.startMonth.plusDays(5),
                        month = startMonth
                    ),
                    Day(
                        date = this.startMonth.plusDays(9),
                        month = startMonth
                    )
                )
            )
        )

        state.selection.clear()

        val nodeSix = rule.onNodeWithText("6")
        val nodeTen = rule.onNodeWithText("10")

        val resultSix = nodeSix.getTextLayoutResult()
        val resultTen = nodeTen.getTextLayoutResult()

        assertEquals(colorScheme.onSurface, resultSix.layoutInput.style.color)
        assertEquals(colorScheme.onSurface, resultTen.layoutInput.style.color)
    }

    @Test
    fun swipeUpdatesState() {
        val startDayOfWeek = DayOfWeek.MONDAY
        val startMonth = Month(
            date = startMonth,
            startOfWeek = startDayOfWeek
        )

        createCalendar(
            modifier = Modifier.fillMaxWidth(),
            initialMonth = startMonth,
            startOfWeek = startDayOfWeek
        )

        val nextMonth = state.months.last()

        rule.mainClock.autoAdvance = false

        onCalendar().performTouchInput {
            swipeLeft()
        }
        rule.mainClock.advanceTimeByFrame()

        assertEquals(startMonth, state.settledMonth)
        assertEquals(nextMonth, state.targetMonth)
        assertTrue(state.pagerState.isScrollInProgress)

        rule.mainClock.advanceTimeUntil(timeoutMillis = 5_000L) { state.settledMonth == nextMonth }

        assertEquals(nextMonth, state.settledMonth)
        assertEquals(nextMonth, state.targetMonth)
        assertFalse(state.pagerState.isScrollInProgress)

        onCalendar().performTouchInput {
            swipeRight()
        }
        rule.mainClock.advanceTimeByFrame()

        assertEquals(nextMonth, state.settledMonth)
        assertEquals(startMonth, state.targetMonth)
        assertTrue(state.pagerState.isScrollInProgress)

        rule.mainClock.advanceTimeUntil(timeoutMillis = 5_000L) { state.settledMonth == startMonth }

        assertEquals(startMonth, state.settledMonth)
        assertEquals(startMonth, state.targetMonth)
        assertFalse(state.pagerState.isScrollInProgress)
    }

    @Test
    fun stateIsRestored() {
        val startDayOfWeek = DayOfWeek.MONDAY
        val startMonth = Month(
            date = startMonth,
            startOfWeek = startDayOfWeek
        )
        val selection = Selection(
            listOf(
                Day(
                    date = this.startMonth.plusDays(5),
                    month = startMonth
                ),
                Day(
                    date = this.startMonth.plusDays(9),
                    month = startMonth
                )
            )
        )

        createCalendar(
            modifier = Modifier.fillMaxWidth(),
            initialMonth = startMonth,
            startOfWeek = startDayOfWeek,
            initialSelection = selection
        )

        assertEquals(startMonth, state.settledMonth)
        assertEquals(startDayOfWeek, state.startOfWeek)
        assertEquals(selection, state.selection)

        restorationTester.emulateSavedInstanceStateRestore()

        assertEquals(startMonth, state.settledMonth)
        assertEquals(startDayOfWeek, state.startOfWeek)
        assertEquals(selection, state.selection)
    }

    private fun SemanticsNodeInteraction.getTextLayoutResult(): TextLayoutResult {
        val textLayoutResults = mutableListOf<TextLayoutResult>()

        this.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(textLayoutResults) }

        return textLayoutResults.first()
    }

}