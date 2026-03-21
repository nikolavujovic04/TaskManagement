package com.example.taskmanagement

import android.app.Application
import com.example.taskmanagement.di.Graph

class TaskManagamentApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}