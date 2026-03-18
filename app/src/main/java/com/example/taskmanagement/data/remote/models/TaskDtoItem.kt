package com.example.taskmanagement.data.remote.models


import com.google.gson.annotations.SerializedName

data class TaskDtoItem(
    @SerializedName("description")
    val description: String,
    @SerializedName("dueDate")
    val dueDate: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("isCompleted")
    val isCompleted: Boolean,
    @SerializedName("priority")
    val priority: String,
    @SerializedName("reminderEnabled")
    val reminderEnabled: Boolean,
    @SerializedName("tags")
    val tags: String,
    @SerializedName("title")
    val title: String
)