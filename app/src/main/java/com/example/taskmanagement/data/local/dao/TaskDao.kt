package com.example.taskmanagement.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.taskmanagement.data.local.models.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    //koristi se za definisanje svih operacija baze podataka
    //koje su nam potrebne za funkcije aplikacije
    //komunicira sa bazom

    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueDate DESC")
    fun getAllTasks(): Flow<List<Task>>//vracamo listu taskova
}