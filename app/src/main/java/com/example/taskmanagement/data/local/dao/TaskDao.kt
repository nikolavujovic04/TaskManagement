package com.example.taskmanagement.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

    @Query("SELECT * FROM tasks WHERE dueDate= :date ORDER BY isCompleted ASC")
    fun getTasksDueToday(date: LocalDate): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE dueDate= :today AND isCompleted = 0")
    fun getTasksForDate(today: LocalDate): Flow<List<Task>>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1")
    fun getCompletedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0 AND dueDate < :today")
    fun getOverDueCount(today: LocalDate): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE priority = 'High' AND isCompleted = 0")
    fun getPendingHighPriorityCount(): Flow<Int>

    @Query("SELECT * FROM tasks WHERE dueDate BETWEEN :startDate AND :endDate")
    fun getTaskInDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Task>>

    @Query("SELECT DISTINCT dueDate FROM tasks WHERE dueDate BETWEEN :startDate AND :endDate")
    fun getDatesWithTasks(startDate: LocalDate, endDate: LocalDate): Flow<List<LocalDate>>

    @Query("SELECT * FROM tasks WHERE syncStatus != 'SYNCED'")
    suspend fun getDirtyTasks(): List<Task>

    //CRUD operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)//onConflict znaci sta treba da se radi ukoliko imamo stavku sa tim id-em vec
    suspend fun  insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<Task>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(tasks: List<Task>)
}