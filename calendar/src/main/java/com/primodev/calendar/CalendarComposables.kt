package com.primodev.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.primodev.calendar.models.Day

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    state: CalendarState = rememberCalendarState(),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    headerContent: (@Composable ColumnScope.() -> Unit)? = null,
    dayContent: @Composable RowScope.(Day) -> Unit
) {
    LaunchedEffect(state.pagerState.settledPage, state.pagerState.isScrollInProgress) {
        if(!state.pagerState.isScrollInProgress) {
            val offset = state.pagerState.settledPage - 1L

            state.scrollToMonth(state.settledMonth.plusMonths(offset))
        }
    }

    HorizontalPager(
        modifier = modifier,
        state = state.pagerState
    ) { page ->
        val chunkedDates by remember(page) {
            derivedStateOf {
                val datesForMonth = state.months[page]

                datesForMonth.days.chunked(CalendarState.WEEK_LENGTH)
            }
        }

        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxWidth(),
            verticalArrangement = verticalArrangement
        ) {
            headerContent?.invoke(this)

            chunkedDates.forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = horizontalArrangement
                ) {
                    week.forEach {
                        dayContent(it)
                    }
                }
            }
        }
    }
}