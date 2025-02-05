package com.github.primodev23.calendar.models

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.minus
import kotlinx.datetime.plus

internal fun LocalDate.withDayOfMonth(day: Int) = LocalDate(year, month, day)

internal val LocalDate.nextMonth
    get() = this.withDayOfMonth(1).plus(
        value = 1,
        unit = DateTimeUnit.MONTH
    )

internal fun LocalDate.atEndOfMonth() = this
    .nextMonth
    .minus(
        value = 1,
        unit = DateTimeUnit.DAY
    )

internal val LocalDate.isLeapYear: Boolean
    get() = year and 3 == 0 && (year % 100 != 0 || year % 400 == 0)