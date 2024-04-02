package com.github.primodev23.calendar.models

data class DayRange(
    override val start: Day,
    override val endInclusive: Day
) : ClosedRange<Day>, DayProgression(start, endInclusive)

open class DayProgression(
    private val start: Day,
    private val endInclusive: Day
) : Iterable<Day> {
    override fun iterator(): Iterator<Day> = DayProgressionIterator(start, endInclusive)
}

class DayProgressionIterator(
    start: Day,
    private val endInclusive: Day
) : Iterator<Day> {
    private var hasNext = start <= endInclusive
    var next = if (hasNext) {
        start
    } else {
        endInclusive
    }

    override fun hasNext() = hasNext

    override fun next(): Day {
        val value = next

        if (next == endInclusive) {
            if (!hasNext) {
                throw NoSuchElementException()
            }
            hasNext = false
        } else {
            next += 1L
        }

        return value
    }

}