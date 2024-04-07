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

@Suppress("unused", "MemberVisibilityCanBePrivate")
@Stable
class CalendarState(
    initialMonth: Month,
    initialSelection: Selection,
    initialStartOfWeek: DayOfWeek,
    initialMinMonth: Month,
    initialMaxMonth: Month
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

    var minMonth by mutableStateOf(initialMinMonth)
        private set

    var maxMonth by mutableStateOf(initialMaxMonth)
        private set

    private val pageCount by derivedStateOf {
        minMonth.getMonthsBetween(maxMonth, endInclusive = true)
    }

    internal val pagerState = CalendarPagerState(
        updatedPageCount = {
            pageCount
        },
        initialCurrentPage = getPageForMonth(initialMonth)
    )

    var startOfWeek by mutableStateOf(initialStartOfWeek)
        private set

    internal val minMonthWithDayOfWeek by derivedStateOf {
        minMonth.copy(startOfWeek = startOfWeek)
    }

    val settledMonth by derivedStateOf {
        minMonthWithDayOfWeek.plusMonths(pagerState.settledPage.toLong())
    }

    val targetMonth by derivedStateOf {
        minMonthWithDayOfWeek.plusMonths(pagerState.targetPage.toLong())
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

    suspend fun updateMinMonth(month: Month) {
        assert(month.startDate <= maxMonth.startDate) {
            "month cannot be after the set maximum month"
        }

        val newCurrentPage = if (settledMonth.startDate < month.startDate) {
            0
        } else {
            getPageForMonth(
                month = settledMonth,
                minMonth = month
            )
        }

        minMonth = month
        pagerState.scrollToPage(newCurrentPage)
    }

    suspend fun updateMaxMonth(month: Month) {
        assert(month.startDate >= minMonth.startDate) {
            "month cannot be before the set minimum month"
        }

        maxMonth = month

        if (settledMonth.startDate > month.startDate) {
            val newPageCount = getPageForMonth(month)

            pagerState.scrollToPage(newPageCount)
        }
    }

    private fun Month.validateBounds() {
        assert(this.startDate >= minMonth.startDate) {
            "month cannot be before the set minimum month"
        }
        assert(this.startDate <= maxMonth.startDate) {
            "month cannot be after the set maximum month"
        }
    }

    private fun getPageForMonth(
        month: Month,
        minMonth: Month = this.minMonth
    ): Int {
        return minMonth.getMonthsBetween(month)
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
                    it.startOfWeek,
                    it.minMonth,
                    it.maxMonth
                )
            },
            restore = {
                CalendarState(
                    initialMonth = it[0] as Month,
                    initialSelection = it[1] as Selection,
                    initialStartOfWeek = it[2] as DayOfWeek,
                    initialMinMonth = it[3] as Month,
                    initialMaxMonth = it[4] as Month
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