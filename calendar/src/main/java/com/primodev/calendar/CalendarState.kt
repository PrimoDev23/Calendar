package com.primodev.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.primodev.calendar.models.Month
import java.time.LocalDate

@Suppress("unused")
@Stable
class CalendarState(initialMonth: Month) {
    internal val pagerState = CalendarPagerState()
    var currentMonth by mutableStateOf(initialMonth)
        private set

    internal val months by derivedStateOf {
        (-1L..1L step 1L).map { offset ->
            currentMonth.plusMonths(offset)
        }
    }

    suspend fun scrollToMonth(month: Month) {
        pagerState.scrollToPage(1)
        currentMonth = month
    }

    suspend fun animateScrollToNextMonth() {
        pagerState.animateScrollToPage(2)
    }

    suspend fun animateScrollToPreviousMonth() {
        pagerState.animateScrollToPage(0)
    }

    companion object {
        internal const val WEEK_LENGTH = 7

        internal val Saver: Saver<CalendarState, *> = listSaver(
            save = {
                listOf(it.currentMonth.startDate)
            },
            restore = {
                CalendarState(initialMonth = Month(it[0]))
            }
        )
    }

}

@Composable
fun rememberCalendarState(initialMonth: Month = Month(date = LocalDate.now())): CalendarState {
    return rememberSaveable(saver = CalendarState.Saver) {
        CalendarState(initialMonth = initialMonth)
    }
}