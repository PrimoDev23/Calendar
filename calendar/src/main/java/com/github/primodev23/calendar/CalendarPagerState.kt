package com.github.primodev23.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState

@OptIn(ExperimentalFoundationApi::class)
class CalendarPagerState: PagerState(currentPage = 1) {
    override val pageCount: Int = 3
}