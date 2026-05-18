package com.example.taskmanagement.presentation.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmanagement.data.local.models.Task
import java.time.LocalDate
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.taskmanagement.presentation.my_tasks.components.TaskItemComponent
import java.time.format.DateTimeFormatter

@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
}

@Composable
private fun CalendarScreen(
    modifier: Modifier = Modifier,
    state: CalendarUiState,
    onDateSelected: (LocalDate) -> Unit,
    onNextMonth: () -> Unit,
    onPreviousMonth: () -> Unit,
    onNextWeek: () -> Unit,
    onPreviousWeek: () -> Unit,
    onViewChanged: (CalendarView) -> Unit,
    onTaskCheckedChange: (Task, Boolean) -> Unit
    ) {
    val today = remember { LocalDate.now() }
    Column(
        modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            SegmentedButton(
                modifier = Modifier.fillMaxWidth(0.6f),
                selectedItem = if (state.selectedView == CalendarView.MONTH) "Month" else "Week",
                items = listOf("Month","Week"),
                onItemClick = {
                    onViewChanged(
                        if (it == "Month") CalendarView.MONTH else CalendarView.WEEK)
                }
            )
        }
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val isMonthView = state.selectedView == CalendarView.MONTH
            IconButton(
                onClick = {
                    if(isMonthView) onPreviousMonth() else onPreviousWeek()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                    contentDescription = "Previous"
                )
            }
            Text(
                text = state.currentMonth.format(DateTimeFormatter.ofPattern(
                    if (isMonthView) "MMMM yyyy" else "dd MMm"
                )),
                style = MaterialTheme.typography.titleLarge
            )
            IconButton(
                onClick = {
                    if(isMonthView) onNextMonth() else onNextWeek()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                    contentDescription = "Next"
                )
            }

        }
        Spacer(Modifier.height(16.dp))
        if(state.selectedView == CalendarView.MONTH){
            CalendarGrid(
                selectedDate = state.selectedDate,
                currentMonth = state.currentMonth,
                markedDates = state.markedDatesInMonth,
                onDateSelected = onDateSelected,
                today = today
            )
        }
        else{
            WeeklyCalendarGrid(
                currentWeekStartDate = state.currentWeekMonday,
                selectedDate = state.selectedDate,
                markedDates = state.markedDatesInMonth,
                onDateSelected = onDateSelected,
                today = today

            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = state.selectedDate.format(DateTimeFormatter.ofPattern("MMMM d")),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(16.dp))
        LazyColumn {
            items(state.tasksForSelectedDate,key = {it.id}){ task ->
                TaskItemComponent(
                    task = task,
                    onCheckedChange = {isChecked -> onTaskCheckedChange(task,isChecked) }
                )
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun CalendarScreenPrev() {
    CalendarScreen(
        state = CalendarUiState(),
        onDateSelected = {},
        onNextMonth = {},
        onPreviousMonth = {},
        onNextWeek = {},
        onPreviousWeek = {},
        onViewChanged = {},
        onTaskCheckedChange = {_,_ ->}
    )
}