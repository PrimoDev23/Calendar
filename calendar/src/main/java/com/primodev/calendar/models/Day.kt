package com.primodev.calendar.models

import androidx.compose.runtime.Immutable
import java.time.LocalDate

@Immutable
data class Day(
    val date: LocalDate,
    val isInSelectedMonth: Boolean
)
