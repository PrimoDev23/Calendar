package com.github.primodev23.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.github.primodev23.calendar.models.Month
import com.github.primodev23.calendar.models.Selection
import java.time.DayOfWeek
import java.time.LocalDate

@Suppress("unused")
@Stable
class CalendarState(
    initialMonth: Month,
    initialSelection: Selection,
    initialStartOfWeek: DayOfWeek
) {
    internal val pagerState = CalendarPagerState()

    var startOfWeek by mutableStateOf(initialStartOfWeek)
        private set

    var settledMonth by mutableStateOf(initialMonth.copy(startOfWeek = startOfWeek))
        private set

    val targetMonth by derivedStateOf {
        val offset = pagerState.targetPage - 1L

        settledMonth.plusMonths(offset)
    }

    val selection = initialSelection

    internal val months by derivedStateOf {
        (-1L..1L step 1L).map { offset ->
            settledMonth.plusMonths(offset)
        }
    }

    suspend fun scrollToMonth(month: Month) {
        pagerState.scrollToPage(1)
        settledMonth = month.copy(startOfWeek = startOfWeek)
    }

    suspend fun animateScrollToNextMonth() {
        pagerState.animateScrollToPage(2)
    }

    suspend fun animateScrollToPreviousMonth() {
        pagerState.animateScrollToPage(0)
    }

    fun updateStartOfWeek(dayOfWeek: DayOfWeek) {
        startOfWeek = dayOfWeek
        settledMonth = settledMonth.copy(startOfWeek = dayOfWeek)
    }

    companion object {
        internal const val WEEK_LENGTH = 7

        internal val Saver: Saver<CalendarState, *> = listSaver(
            save = {
                listOf(
                    it.settledMonth,
                    it.selection,
                    it.startOfWeek
                )
            },
            restore = {
                CalendarState(
                    initialMonth = it[0] as Month,
                    initialSelection = it[1] as Selection,
                    initialStartOfWeek = it[2] as DayOfWeek
                )
            }
        )
    }

}

@Composable
fun rememberCalendarState(
    initialMonth: Month = Month(date = LocalDate.now()),
    initialSelection: Selection = Selection(),
    startOfWeek: DayOfWeek = DayOfWeek.MONDAY
): CalendarState {
    return rememberSaveable(saver = CalendarState.Saver) {
        CalendarState(
            initialMonth = initialMonth,
            initialSelection = initialSelection,
            initialStartOfWeek = startOfWeek
        )
    }
}