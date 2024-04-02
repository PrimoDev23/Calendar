package com.github.primodev23.calendar.models

import androidx.compose.runtime.Stable
import androidx.compose.runtime.toMutableStateList
import java.io.Serializable

@Stable
class Selection(initialSelection: List<Day> = emptyList()) : Serializable {

    internal val selectedDays = initialSelection.toMutableStateList()

    fun contains(day: Day): Boolean {
        return selectedDays.any { it.compareTo(day) == 0 }
    }

    fun add(day: Day) {
        if (!contains(day)) {
            selectedDays.add(day)
        }
    }

    fun remove(day: Day) {
        if (contains(day)) {
            selectedDays.removeIf {
                it.compareTo(day) == 0
            }
        }
    }

    fun addAll(days: Collection<Day>) {
        selectedDays.addAll(days)
    }

    fun clear() {
        selectedDays.clear()
    }

}