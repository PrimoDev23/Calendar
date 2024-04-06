package com.github.primodev23.calendarshowcase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.primodev23.calendar.Calendar
import com.github.primodev23.calendar.models.Day
import com.github.primodev23.calendar.models.Month
import com.github.primodev23.calendar.rememberCalendarState
import com.github.primodev23.calendarshowcase.ui.theme.CalendarShowcaseTheme
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Collections
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalendarShowcaseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalendarContent(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalendarContent(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        val scope = rememberCoroutineScope()
        val state = rememberCalendarState(
            initialMinMonth = Month(date = LocalDate.now().minusMonths(1)),
            initialMaxMonth = Month(date = LocalDate.now().plusMonths(1))
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    scope.launch {
                        state.animateScrollToPreviousMonth()
                    }
                },
                enabled = state.settledMonth.startDate > state.minMonth.startDate
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                    contentDescription = null
                )
            }

            val monthString = state.targetMonth.startDate.month.getDisplayName(
                TextStyle.FULL,
                Locale.getDefault()
            )
            val fullText = "$monthString ${state.targetMonth.startDate.year}"

            Crossfade(
                modifier = Modifier.weight(1f),
                targetState = fullText,
                label = "MonthLabel"
            ) { text ->
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = text,
                    textAlign = TextAlign.Center
                )
            }

            IconButton(
                onClick = {
                    scope.launch {
                        state.animateScrollToNextMonth()
                    }
                },
                enabled = state.settledMonth.startDate < state.maxMonth.startDate
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                    contentDescription = null
                )
            }
        }

        var rangeSelection by remember {
            mutableStateOf(false)
        }
        var startDay by remember {
            mutableStateOf<Day?>(null)
        }

        Calendar(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp),
            state = state,
            headerContent = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val entries = remember(state.startOfWeek) {
                        val dayOfWeekEntries = DayOfWeek.entries.toMutableList()
                        val distance = -state.startOfWeek.ordinal

                        Collections.rotate(dayOfWeekEntries, distance)

                        dayOfWeekEntries
                    }

                    entries.forEach {
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = it.getDisplayName(TextStyle.SHORT, Locale.getDefault()))
                        }
                    }
                }
            }
        ) { date ->
            Day(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                date = date,
                onClick = {
                    if (rangeSelection) {
                        val innerStartDay = startDay

                        if (innerStartDay != null) {
                            val nextStartDay = innerStartDay + 1
                            val range = nextStartDay..date

                            startDay = null
                            state.selection.addAll(range.toList())
                        } else {
                            startDay = date
                            state.selection.clear()
                            state.selection.add(date)
                        }
                    } else {
                        if (state.selection.contains(date)) {
                            state.selection.remove(date)
                        } else {
                            state.selection.add(date)
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            RadioButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.single_selection),
                selected = !rangeSelection,
                onClick = {
                    rangeSelection = false
                    startDay = null
                }
            )

            RadioButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.range_selection),
                selected = rangeSelection,
                onClick = {
                    rangeSelection = true
                    startDay = null
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            DayOfWeek.entries.forEach {
                FilterChip(
                    selected = state.startOfWeek == it,
                    onClick = {
                        state.updateStartOfWeek(it)
                    },
                    label = {
                        Text(text = it.getDisplayName(TextStyle.SHORT, Locale.getDefault()))
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            state.updateMinMonth(state.minMonth.plusMonths(-1))
                        }
                    }
                ) {
                    Text(text = stringResource(id = R.string.min_month_minus_one))
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            state.updateMaxMonth(state.maxMonth.plusMonths(-1))
                        }
                    }
                ) {
                    Text(text = stringResource(id = R.string.max_month_minus_one))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            state.updateMinMonth(state.minMonth.plusMonths(1))
                        }
                    }
                ) {
                    Text(text = stringResource(id = R.string.min_month_plus_one))
                }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            state.updateMaxMonth(state.maxMonth.plusMonths(1))
                        }
                    }
                ) {
                    Text(text = stringResource(id = R.string.max_month_plus_one))
                }
            }
        }
    }
}

@Composable
fun RadioButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        androidx.compose.material3.RadioButton(
            selected = selected,
            onClick = onClick
        )

        Text(text = text)
    }
}

@Preview
@Composable
private fun CalendarContentPreview() {
    Surface(modifier = Modifier.fillMaxWidth()) {
        CalendarContent(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun Day(
    date: Day,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = if (date.isInSelectedMonth) {
                MaterialTheme.colorScheme.surfaceContainerHighest
            } else {
                MaterialTheme.colorScheme.surfaceContainerLow
            }
        ),
        onClick = onClick,
        border = if (date.isSelected) {
            BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            null
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = date.date.dayOfMonth.toString())
        }
    }
}

