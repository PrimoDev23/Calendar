package com.github.primodev23.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.github.primodev23.calendar.models.Month
import com.github.primodev23.calendar.models.Selection
import java.time.DayOfWeek
import java.time.LocalDate

@Suppress("unused", "MemberVisibilityCanBePrivate")
@Stable
class CalendarState(
    initialMonth: Month,
    initialSelection: Selection,
    initialStartOfWeek: DayOfWeek,
    private val initialMinMonth: Month = initialMonth.plusMonths(-DEFAULT_MONTH_LIMIT),
    private val initialMaxMonth: Month = initialMonth.plusMonths(DEFAULT_MONTH_LIMIT)
) {
    init {
        assert(initialMinMonth.startDate <= initialMaxMonth.startDate) {
            "initialMinMonth cannot be after initialMaxMonth"
        }
        assert(initialMinMonth.startDate <= initialMonth.startDate) {
            "initialMinMonth cannot be after initialMonth"
        }
        assert(initialMaxMonth.startDate >= initialMonth.startDate) {
            "initialMaxMonth cannot be before initialMonth"
        }
    }

    internal val initialPage by mutableIntStateOf(getPageForMonth(initialMonth))

    internal var pagerState = CalendarPagerState(
        initialPageCount = initialMinMonth.getMonthsBetween(initialMaxMonth),
        initialCurrentPage = initialPage
    )

    var startOfWeek by mutableStateOf(initialStartOfWeek)
        private set

    internal val initialMonthWithDayOfWeek by derivedStateOf {
        initialMonth.copy(startOfWeek = startOfWeek)
    }

    val settledMonth by derivedStateOf {
        val offsetFromInitialPage = pagerState.settledPage - initialPage

        initialMonthWithDayOfWeek.plusMonths(offsetFromInitialPage.toLong())
    }

    val targetMonth by derivedStateOf {
        val offsetFromInitialPage = pagerState.targetPage - initialPage

        initialMonthWithDayOfWeek.plusMonths(offsetFromInitialPage.toLong())
    }

    val selection = initialSelection

    suspend fun scrollToMonth(month: Month) {
        month.validateBounds()

        val page = getPageForMonth(month)
        pagerState.scrollToPage(page)
    }

    suspend fun animateScrollToMonth(month: Month) {
        month.validateBounds()

        val page = getPageForMonth(month)
        pagerState.animateScrollToPage(page)
    }

    suspend fun animateScrollToNextMonth() {
        val nextMonth = settledMonth.plusMonths(1)

        animateScrollToMonth(nextMonth)
    }

    suspend fun animateScrollToPreviousMonth() {
        val previousMonth = settledMonth.plusMonths(-1)

        animateScrollToMonth(previousMonth)
    }

    fun updateStartOfWeek(dayOfWeek: DayOfWeek) {
        startOfWeek = dayOfWeek
    }

    private fun Month.validateBounds() {
        assert(this.startDate >= initialMinMonth.startDate) {
            "month cannot be before the set minimum month"
        }
        assert(this.startDate <= initialMaxMonth.startDate) {
            "month cannot be after the set maximum month"
        }
    }

    private fun getPageForMonth(month: Month): Int {
        return initialMinMonth.getMonthsBetween(month)
    }

    companion object {
        internal const val WEEK_LENGTH = 7

        // 10 years - 1 current month
        internal const val DEFAULT_MONTH_LIMIT = 10 * 12L

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
    startOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    initialMinMonth: Month = initialMonth.plusMonths(-CalendarState.DEFAULT_MONTH_LIMIT),
    initialMaxMonth: Month = initialMonth.plusMonths(CalendarState.DEFAULT_MONTH_LIMIT)
): CalendarState {
    return rememberSaveable(saver = CalendarState.Saver) {
        CalendarState(
            initialMonth = initialMonth,
            initialSelection = initialSelection,
            initialStartOfWeek = startOfWeek,
            initialMinMonth = initialMinMonth,
            initialMaxMonth = initialMaxMonth
        )
    }
}