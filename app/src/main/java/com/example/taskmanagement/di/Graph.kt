package com.example.taskmanagement.di

import com.example.taskmanagement.data.remote.TaskApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Graph {
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
}