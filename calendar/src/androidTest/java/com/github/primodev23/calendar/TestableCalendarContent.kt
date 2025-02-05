package com.github.primodev23.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.datetime.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarContent(
    state: CalendarState,
    modifier: Modifier = Modifier
) {
    Calendar(
        modifier = modifier,
        state = state,
        headerContent = {
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        val dayOfWeekInt = state.startOfWeek.ordinal + it
                        val dayOfWeek = DayOfWeek.entries[dayOfWeekInt % 7]

                        Text(
                            text = dayOfWeek.getDisplayName(
                                TextStyle.SHORT,
                                Locale.getDefault()
                            )
                        )
                    }
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
        ) {
            Text(
                text = it.date.dayOfMonth.toString(),
                color = if (it.isSelected) {
                    Color.Red
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}