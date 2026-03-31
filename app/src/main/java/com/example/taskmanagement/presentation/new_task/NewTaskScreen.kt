package com.example.taskmanagement.presentation.new_task

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmanagement.presentation.my_tasks.Priority
import com.example.taskmanagement.presentation.my_tasks.TaskTag
import java.time.LocalDate
import java.util.Calendar

@Composable
fun NewTaskScreen(
    modifier: Modifier = Modifier,
    viewModel: NewTaskViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(uiState.isTaskSaved) {
        if(uiState.isTaskSaved){
            onNavigateBack()
            viewModel.onTaskSavedHandled()
        }
    }
}

@Composable
private fun NewTaskScreen(
    modifier: Modifier = Modifier,
    state: NewTaskUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDueDateChange: (LocalDate) -> Unit,
    onPriorityChange: (Priority) -> Unit,
    onTagChange: (TaskTag) -> Unit,
    onReminderChange: (Boolean) -> Unit,
    onCreateTask: () -> Unit,
    onErrorShown: () -> Unit
    ) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            onDueDateChange(LocalDate.of(year, month + 1, dayOfMonth))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = state.title,
            onValueChange = onTitleChange,
            label = { Text(text = "Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = state.description,
            onValueChange = onDescriptionChange,
            label = { Text(text = "Description") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 12.dp),
            maxLines = 5
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = state.dueDate.toString(),
            onValueChange = {},
            label = { Text(text = "Due date") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth().clickable{datePickerDialog.show()}
        )
    }
}