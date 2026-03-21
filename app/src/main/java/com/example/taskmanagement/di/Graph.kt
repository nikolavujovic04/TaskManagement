package com.example.taskmanagement.di

import android.content.Context
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.taskmanagement.data.local.AppDatabase
import com.example.taskmanagement.data.remote.TaskApiService
import com.example.taskmanagement.data.repository.TaskRepository
import com.example.taskmanagement.data.repository.TaskRepositoryImpl
import com.example.taskmanagement.data.worker.SyncWorker
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Graph {
    lateinit var repository: TaskRepository
    private const val BASE_URL = "https://69bb17200915748735b867d9.mockapi.io/api/"
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // pretvara se iz JSON u Kotlin
            .build()
    }

    val apiService: TaskApiService by lazy {
        retrofit.create(TaskApiService::class.java)
    }

    fun provide(ctx: Context){
        repository = TaskRepositoryImpl(
            taskDao = AppDatabase.getDatabase(ctx).taskDao(),
            apiService = apiService,
            workManager = WorkManager.getInstance(ctx)
        )
    }

    private fun setPeriodicSyncRequest(ctx: Context){
        val periodicSyncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            1, TimeUnit.HOURS)//periodicni zahtev na jedan sat
    }
}