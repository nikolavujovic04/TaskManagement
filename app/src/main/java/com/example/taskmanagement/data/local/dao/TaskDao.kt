package com.example.taskmanagement.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.taskmanagement.data.local.models.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TaskDao {
    //koristi se za definisanje svih operacija baze podataka
    //koje su nam potrebne za funkcije aplikacije
    //komunicira sa bazom

    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueDate DESC")
    fun getAllTasks(): Flow<List<Task>>//vracamo listu taskova

    @Query("SELECT * FROM tasks WHERE dueDate= :today AND isCompleted = 0")
    fun getTasksDueToday(today: LocalDate): Flow<List<Task>>

    @Query("SELECT COUNT() WHERE isCompleted = 1")
    fun getCompletedCount(): Flow<Int>
}