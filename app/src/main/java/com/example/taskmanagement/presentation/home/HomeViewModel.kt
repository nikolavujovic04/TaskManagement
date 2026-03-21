package com.example.taskmanagement.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanagement.data.local.models.Task
import com.example.taskmanagement.data.repository.TaskRepository
import com.example.taskmanagement.di.Graph
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class SyncStatus{
    IDLE,
    SYNCING,
    SUCCESS
}

enum class SortOrder{
    DEFAULT,
    BY_PRIORITY_DESC,
    BY_TITLE_ASC
}

data class HomeUiState(
    val tasks: List<Task> = emptyList(),// lista zadataka koje cemo dobiti
    val completedCount: Int = 0,
    val remainingCount: Int = 0,
    val syncStatus: SyncStatus = SyncStatus.IDLE,
    val sortOrder: SortOrder = SortOrder.DEFAULT
)

class HomeViewModel(// trazi podatke iz skladista podataka, transformise ih i prikazuje na korisnickom interfejsu
    private val taskRepository: TaskRepository = Graph.repository //interfejs deklarisemo ovde
): ViewModel(){
    private val _sortOrder = MutableStateFlow(SortOrder.DEFAULT)
    private val _syncStatus = MutableStateFlow(SyncStatus.IDLE)
    private val _todayTasksFlow = taskRepository.getTasksForDate(LocalDate.now())

    //razlika izmedju MutableStateFlow je sto taj mozemo da promenimo a StateFlow ne mozemo
    //StateFlow je samo za citanje, tako da ako neko pokusa da ga promeni dobice poruku o gresci
    val uiState: StateFlow<HomeUiState> = combine(
        _todayTasksFlow,
        _sortOrder,
        _syncStatus
    ){tasks,sortOrder,syncStatus ->
        val sortedTasks = sortedTasks(tasks, sortOrder)
        val completedCount = sortedTasks.count{ it.isCompleted }
        val remainingCount = sortedTasks.size - completedCount
        HomeUiState(
            tasks = sortedTasks,
            completedCount = completedCount,
            remainingCount = remainingCount,
            syncStatus = syncStatus,
            sortOrder = sortOrder
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    private fun sortedTasks(tasks: List<Task>, sortOrder: SortOrder): List<Task>{
        return when(sortOrder){
            SortOrder.DEFAULT -> tasks
            SortOrder.BY_PRIORITY_DESC -> tasks.sortedByDescending { it.priority }
            SortOrder.BY_TITLE_ASC -> tasks.sortedBy { it.title }
        }
    }

    fun onRefresh(){
        viewModelScope.launch {
            if(_syncStatus.value == SyncStatus.SYNCING) return@launch
            _syncStatus.value = SyncStatus.SYNCING

            try{
                taskRepository.refreshTasksFromServer()
                _syncStatus.value = SyncStatus.SUCCESS
                delay(3000)
            }catch(e: Exception){
                e.printStackTrace()
            }finally {
                _syncStatus.value = SyncStatus.IDLE
            }
        }
    }

    fun onTaskCheckedChange(task: Task, isCompleted: Boolean){
        viewModelScope.launch {
            taskRepository.updateTask(task.copy(isCompleted = isCompleted))
        }
    }

    fun onSortChange(sortOrder: SortOrder){
        _sortOrder.value = sortOrder
    }
}