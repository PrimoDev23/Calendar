package com.github.primodev23.calendar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.text.TextLayoutResult
import com.github.primodev23.calendar.models.Day
import com.github.primodev23.calendar.models.Month
import com.github.primodev23.calendar.models.Selection
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class CalendarComposablesTest : BaseCalendarTest() {

    private val startMonth = LocalDate(2024, 3, 1)

    @Test
    fun init_validation() {
        val startDayOfWeek = DayOfWeek.MONDAY
        val startMonth = Month(
            date = startMonth,
            startOfWeek = startDayOfWeek
        )

        assertThrows(
            "initialMinMonth cannot be after initialMaxMonth",
            AssertionError::class.java
        ) {
            val minMonth = startMonth - 2
            val maxMonth = minMonth - 1

            CalendarState(
                initialMonth = startMonth,
                initialSelection = Selection(),
                initialStartOfWeek = startDayOfWeek,
                initialMinMonth = minMonth,
                initialMaxMonth = maxMonth
            )
        }

        assertThrows("initialMinMonth cannot be after initialMonth", AssertionError::class.java) {
            val minMonth = startMonth + 2
            val maxMonth = minMonth + 1

            CalendarState(
                initialMonth = startMonth,
                initialSelection = Selection(),
                initialStartOfWeek = startDayOfWeek,
                initialMinMonth = minMonth,
                initialMaxMonth = maxMonth
            )
        }

        assertThrows("initialMaxMonth cannot be before initialMonth", AssertionError::class.java) {
            val minMonth = startMonth - 2
            val maxMonth = startMonth - 1

            CalendarState(
                initialMonth = startMonth,
                initialSelection = Selection(),
                initialStartOfWeek = startDayOfWeek,
                initialMinMonth = minMonth,
                initialMaxMonth = maxMonth
            )
        }
    }

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

        val nextMonth = state.settledMonth + 1

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

        val previousMonth = state.settledMonth - 1

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
    fun scrollToMonth_validateBounds() {
        val startDayOfWeek = DayOfWeek.MONDAY
        val startMonth = Month(
            date = startMonth,
            startOfWeek = startDayOfWeek
        )
        val minMonth = startMonth - 5
        val outOfBoundsMin = minMonth - 1

        val maxMonth = startMonth + 5
        val outOfBoundsMax = maxMonth + 1

        createCalendar(
            modifier = Modifier.fillMaxWidth(),
            initialMonth = startMonth,
            startOfWeek = startDayOfWeek,
            initialMinMonth = minMonth,
            initialMaxMonth = maxMonth
        )

        assertThrows(AssertionError("month cannot be before the set minimum month")) {
            state.scrollToMonth(outOfBoundsMin)
        }

        assertThrows(AssertionError("month cannot be after the set maximum month")) {
            state.scrollToMonth(outOfBoundsMax)
        }
    }

    @Test
    fun scrollToMonth() {
        val startDayOfWeek = DayOfWeek.MONDAY
        val startMonth = Month(
            date = startMonth,
            startOfWeek = startDayOfWeek
        )
        val newMonth = Month(date = LocalDate(2024, 12, 1))

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
            date = this.startMonth.plus(
                value = 5,
                unit = DateTimeUnit.DAY
            ),
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
            date = this.startMonth.plus(
                value = 5,
                unit = DateTimeUnit.DAY
            ),
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
            date = this.startMonth.plus(
                value = 5,
                unit = DateTimeUnit.DAY
            ),
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
                date = this.startMonth.plus(
                    value = 5,
                    unit = DateTimeUnit.DAY
                ),
                month = startMonth
            ),
            Day(
                date = this.startMonth.plus(
                    value = 9,
                    unit = DateTimeUnit.DAY
                ),
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
                        date = this.startMonth.plus(
                            value = 5,
                            unit = DateTimeUnit.DAY
                        ),
                        month = startMonth
                    ),
                    Day(
                        date = this.startMonth.plus(
                            value = 9,
                            unit = DateTimeUnit.DAY
                        ),
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

        val nextMonth = state.settledMonth + 1

        rule.mainClock.autoAdvance = false

        onCalendar().performTouchInput {
            down(
                Offset(
                    x = right * 0.75f,
                    y = centerY
                )
            )
            moveBy(
                Offset(
                    x = -right * 0.6f,
                    y = 0f
                )
            )
        }

        assertEquals(startMonth, state.settledMonth)
        assertEquals(nextMonth, state.targetMonth)
        assertTrue(state.pagerState.isScrollInProgress)

        onCalendar().performTouchInput {
            up()
        }

        rule.mainClock.advanceTimeUntil(timeoutMillis = 5_000L) {
            state.settledMonth == nextMonth
                    && !state.pagerState.isScrollInProgress
        }

        assertEquals(nextMonth, state.settledMonth)
        assertEquals(nextMonth, state.targetMonth)

        onCalendar().performTouchInput {
            down(
                Offset(
                    x = right * 0.25f,
                    y = centerY
                )
            )
            moveBy(
                Offset(
                    x = right * 0.6f,
                    y = 0f
                )
            )
        }

        assertEquals(nextMonth, state.settledMonth)
        assertEquals(startMonth, state.targetMonth)
        assertTrue(state.pagerState.isScrollInProgress)

        onCalendar().performTouchInput {
            up()
        }

        rule.mainClock.advanceTimeUntil(timeoutMillis = 5_000L) {
            state.settledMonth == startMonth
                    && !state.pagerState.isScrollInProgress
        }

        assertEquals(startMonth, state.settledMonth)
        assertEquals(startMonth, state.targetMonth)
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
                    date = this.startMonth.plus(
                        value = 5,
                        unit = DateTimeUnit.DAY
                    ),
                    month = startMonth
                ),
                Day(
                    date = this.startMonth.plus(
                        value = 9,
                        unit = DateTimeUnit.DAY
                    ),
                    month = startMonth
                )
            )
        )
        val minMonth = startMonth - 3
        val maxMonth = startMonth + 5

        createCalendar(
            modifier = Modifier.fillMaxWidth(),
            initialMonth = startMonth,
            startOfWeek = startDayOfWeek,
            initialSelection = selection,
            initialMinMonth = minMonth,
            initialMaxMonth = maxMonth
        )

        assertEquals(startMonth, state.settledMonth)
        assertEquals(startDayOfWeek, state.startOfWeek)
        assertEquals(selection, state.selection)
        assertEquals(minMonth, state.minMonth)
        assertEquals(maxMonth, state.maxMonth)

        restorationTester.emulateSavedInstanceStateRestore()

        assertEquals(startMonth, state.settledMonth)
        assertEquals(startDayOfWeek, state.startOfWeek)
        assertEquals(selection, state.selection)
        assertEquals(minMonth, state.minMonth)
        assertEquals(maxMonth, state.maxMonth)
    }

    @Test
    fun minMaxMonthApplied() {
        val startDayOfWeek = DayOfWeek.MONDAY
        val startMonth = Month(
            date = startMonth,
            startOfWeek = startDayOfWeek
        )
        val minMonth = startMonth - 3
        val maxMonth = startMonth + 5

        createCalendar(
            modifier = Modifier.fillMaxWidth(),
            initialMonth = startMonth,
            startOfWeek = startDayOfWeek,
            initialMinMonth = minMonth,
            initialMaxMonth = maxMonth
        )

        assertEquals(9, state.pagerState.pageCount)
        assertEquals(3, state.pagerState.settledPage)
        assertEquals(minMonth, state.minMonth)
        assertEquals(maxMonth, state.maxMonth)
    }

    @Test
    fun updateMinMonth_selectedMonthBeforeMinMonth() {
        val startDayOfWeek = DayOfWeek.MONDAY
        val startMonth = Month(
            date = startMonth,
            startOfWeek = startDayOfWeek
        )
        val minMonth = startMonth - 3
        val maxMonth = startMonth + 5

        createCalendar(
            modifier = Modifier.fillMaxWidth(),
            initialMonth = startMonth,
            startOfWeek = startDayOfWeek,
            initialMinMonth = minMonth,
            initialMaxMonth = maxMonth
        )

        rule.runOnUiThread {
            scope.launch {
                state.scrollToMonth(minMonth)
            }
        }
        rule.waitUntil { state.settledMonth == minMonth }

        val newMinMonth = minMonth + 1
        rule.runOnUiThread {
            scope.launch {
                state.updateMinMonth(newMinMonth)
            }
        }
        rule.waitForIdle()

        assertEquals(newMinMonth, state.minMonth)
        assertEquals(newMinMonth, state.settledMonth)
    }

    @Test
    fun updateMinMonth_selectedMonthAfterMinMonth() {
        val startDayOfWeek = DayOfWeek.MONDAY
        val startMonth = Month(
            date = startMonth,
            startOfWeek = startDayOfWeek
        )
        val minMonth = startMonth - 3
        val maxMonth = startMonth + 5

        createCalendar(
            modifier = Modifier.fillMaxWidth(),
            initialMonth = startMonth,
            startOfWeek = startDayOfWeek,
            initialMinMonth = minMonth,
            initialMaxMonth = maxMonth
        )

        val newMinMonth = minMonth + 1
        rule.runOnUiThread {
            scope.launch {
                state.updateMinMonth(newMinMonth)
            }
        }
        rule.waitForIdle()

        assertEquals(newMinMonth, state.minMonth)
        assertEquals(startMonth, state.settledMonth)
    }

    @Test
    fun updateMaxMonth_selectedMonthAfterMaxMonth() {
        val startDayOfWeek = DayOfWeek.MONDAY
        val startMonth = Month(
            date = startMonth,
            startOfWeek = startDayOfWeek
        )
        val minMonth = startMonth - 3
        val maxMonth = startMonth + 5

        createCalendar(
            modifier = Modifier.fillMaxWidth(),
            initialMonth = startMonth,
            startOfWeek = startDayOfWeek,
            initialMinMonth = minMonth,
            initialMaxMonth = maxMonth
        )

        rule.runOnUiThread {
            scope.launch {
                state.scrollToMonth(maxMonth)
            }
        }
        rule.waitUntil { state.settledMonth == maxMonth }

        val newMaxMonth = maxMonth - 1
        rule.runOnUiThread {
            scope.launch {
                state.updateMaxMonth(newMaxMonth)
            }
        }
        rule.waitForIdle()

        assertEquals(newMaxMonth, state.maxMonth)
        assertEquals(newMaxMonth, state.settledMonth)
    }

    @Test
    fun updateMaxMonth_selectedMonthBeforeMaxMonth() {
        val startDayOfWeek = DayOfWeek.MONDAY
        val startMonth = Month(
            date = startMonth,
            startOfWeek = startDayOfWeek
        )
        val minMonth = startMonth - 3
        val maxMonth = startMonth + 5

        createCalendar(
            modifier = Modifier.fillMaxWidth(),
            initialMonth = startMonth,
            startOfWeek = startDayOfWeek,
            initialMinMonth = minMonth,
            initialMaxMonth = maxMonth
        )

        val newMaxMonth = maxMonth - 1
        rule.runOnUiThread {
            scope.launch {
                state.updateMaxMonth(newMaxMonth)
            }
        }
        rule.waitForIdle()

        assertEquals(newMaxMonth, state.maxMonth)
        assertEquals(startMonth, state.settledMonth)
    }

    @Test
    fun animateScrollToMonth() {
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

        val nextMonth = startMonth + 2

        rule.mainClock.autoAdvance = false

        rule.runOnUiThread {
            scope.launch {
                state.animateScrollToMonth(nextMonth)
            }
        }
        rule.mainClock.advanceTimeByFrame()

        assertEquals(nextMonth, state.targetMonth)
        assertEquals(startMonth, state.settledMonth)

        rule.mainClock.advanceTimeBy(1_000)

        assertEquals(nextMonth, state.targetMonth)
        assertEquals(nextMonth, state.settledMonth)
    }

    @Test
    fun animateScrollToMonth_validateBounds() {
        val startDayOfWeek = DayOfWeek.MONDAY
        val startMonth = Month(
            date = startMonth,
            startOfWeek = startDayOfWeek
        )
        val minMonth = startMonth - 5
        val outOfBoundsMin = minMonth - 1

        val maxMonth = startMonth + 5
        val outOfBoundsMax = maxMonth + 1

        createCalendar(
            modifier = Modifier.fillMaxWidth(),
            initialMonth = startMonth,
            startOfWeek = startDayOfWeek,
            initialMinMonth = minMonth,
            initialMaxMonth = maxMonth
        )

        assertThrows(AssertionError("month cannot be before the set minimum month")) {
            state.animateScrollToMonth(outOfBoundsMin)
        }

        assertThrows(AssertionError("month cannot be after the set maximum month")) {
            state.animateScrollToMonth(outOfBoundsMax)
        }
    }

    private fun SemanticsNodeInteraction.getTextLayoutResult(): TextLayoutResult {
        val textLayoutResults = mutableListOf<TextLayoutResult>()

        this.performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(textLayoutResults) }

        return textLayoutResults.first()
    }

}