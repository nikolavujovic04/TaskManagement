package com.example.taskmanagement.data.remote

import com.example.taskmanagement.data.remote.models.TaskDto
import com.example.taskmanagement.data.remote.models.TaskDtoItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TaskApiService {

    @GET("tasks")//zapisujemo sta zelimo da dobijemo, ta tabela se zove tasks
    suspend fun getTasks(): TaskDto //dobija ovaj TaskDto i onda pretvaramo u ono sto razumemo

    @POST("tasks")
    suspend fun createTask(
        @Body task: TaskDtoItem //uvek ovde mora da se navede @Body
    ): TaskDtoItem

    suspend fun updateTask(
        @Path("id") taskId: Int,//Menja ovo id u zagradi sa konkretnom stavkom tj taskId-om
        @Body task: TaskDtoItem //podaci koje zelimo da updejtujemo
    ): TaskDtoItem

    suspend fun deleteTask(
        @Path("id") taskId: Int,
        @Body task: TaskDtoItem
    ): Response<Unit> //jer samo brisemo task, ne primamo ni jednu stavku
}