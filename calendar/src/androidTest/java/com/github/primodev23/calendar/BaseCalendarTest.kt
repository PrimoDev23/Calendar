package com.github.primodev23.calendar

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.github.primodev23.calendar.models.Month
import com.github.primodev23.calendar.models.Selection
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import java.time.DayOfWeek
import java.time.LocalDate

abstract class BaseCalendarTest {

    @get:Rule
    val rule = createComposeRule()

    val restorationTester = StateRestorationTester(rule)

    lateinit var scope: CoroutineScope
    lateinit var state: CalendarState
    lateinit var colorScheme: ColorScheme

    fun createCalendar(
        modifier: Modifier = Modifier,
        initialMonth: Month = Month(date = LocalDate.now()),
        initialSelection: Selection = Selection(),
        startOfWeek: DayOfWeek = DayOfWeek.MONDAY,
        initialMinMonth: Month = initialMonth - 10,
        initialMaxMonth: Month = initialMonth + 10,
    ) {
        restorationTester.setContent {
            scope = rememberCoroutineScope()
            state = rememberCalendarState(
                initialMonth = initialMonth,
                initialSelection = initialSelection,
                startOfWeek = startOfWeek,
                initialMinMonth = initialMinMonth,
                initialMaxMonth = initialMaxMonth
            )
            colorScheme = MaterialTheme.colorScheme

            CalendarContent(
                modifier = modifier.testTag(CalendarTestTag),
                state = state
            )
        }
    }

    fun onCalendar(): SemanticsNodeInteraction {
        return rule.onNodeWithTag(CalendarTestTag)
    }

    inline fun <reified T : Throwable> assertThrows(
        expectedThrowable: T,
        crossinline block: suspend () -> Unit
    ) {
        var exception: Throwable? = null
        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            exception = throwable
        }

        rule.runOnUiThread {
            scope.launch(exceptionHandler) {
                block()
            }
        }

        assertTrue(exception is T)
        assertEquals(expectedThrowable.message, exception?.message)
    }
}

internal const val CalendarTestTag = "Calendar"