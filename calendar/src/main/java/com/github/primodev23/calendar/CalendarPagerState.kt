package com.github.primodev23.calendar

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@OptIn(ExperimentalFoundationApi::class)
class CalendarPagerState(
    updatedPageCount: () -> Int,
    initialCurrentPage: Int
) : PagerState(currentPage = initialCurrentPage) {

    var pageCountState by mutableStateOf(updatedPageCount)
        internal set

    override val pageCount: Int
        get() = pageCountState.invoke()

}