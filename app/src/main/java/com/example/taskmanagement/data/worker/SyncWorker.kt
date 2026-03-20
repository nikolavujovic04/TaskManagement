package com.example.taskmanagement.data.worker

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.taskmanagement.R
import com.example.taskmanagement.data.TaskMapper
import com.example.taskmanagement.data.local.AppDatabase
import com.example.taskmanagement.data.local.models.SyncStatus
import com.example.taskmanagement.di.Graph
import retrofit2.HttpException

class SyncWorker(
    private val appContext: Context,
    params: WorkerParameters
): CoroutineWorker(appContext, params) {
    //sluzi za obavljanje zadataka cak i kada je aplikacija zatvorena, tj prekinuta
    //imamo podatke u bazi koje zelimo da se sinhronizuju sa nasim serverom cak i kada je nasa aplikacija ugasena

    private val taskDao by lazy { AppDatabase.getDatabase(appContext).taskDao() }
    private val apiService by lazy { Graph.apiService }//pravimo grafikon koji mozemo koristiti za kreiranje ove API usluge
    private val notificationManager =
        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val notificationId = 1337

    override suspend fun doWork(): Result {
        val dirtyTasks = taskDao.getDirtyTasks()
        if(dirtyTasks.isEmpty()){
            return Result.success()
        }
        val foregroundInfo = createForegroundInfo("Syncing Tasks...${dirtyTasks.size}")
        setForeground(foregroundInfo)
        try{
            dirtyTasks.forEach { task->
                val taskDtoItem = TaskMapper.mapEntityToDto(task)
                when(task.syncStatus){
                    SyncStatus.CREATED ->{
                        val remoteTask = apiService.createTask(taskDtoItem)
                        taskDao.updateTask(task.copy(
                            remoteId = remoteTask.id.toString(),
                            syncStatus = SyncStatus.SYNCED
                        ))
                    }

                    SyncStatus.UPDATED ->{
                        if(task.remoteId.isBlank()){
                            return@forEach
                        }
                        apiService.updateTask(task.remoteId.toInt(),taskDtoItem)
                        taskDao.updateTask(task.copy(
                            syncStatus = SyncStatus.SYNCED
                        ))
                    }

                    SyncStatus.DELETED ->{
                        if(task.remoteId.isBlank()){
                            taskDao.delete(task)//brisemo jer nije na serveru
                            return@forEach //nastavljamo izvrsavanje sa drugim jer je ovaj null
                        }

                        apiService.deleteTask(
                            task.remoteId.toInt()
                        )
                        taskDao.delete(task)
                    }
                    SyncStatus.SYNCED -> {}
                }

            }
            showNotficationn("Sync Complete","Tasks synced successfully")
            return Result.success()
        }catch (e: HttpException){
            val errorBody = e.response()?.errorBody()?.toString() ?: "unknown error"
            Log.e("SyncWorker", "doWork: errorBody ${errorBody} ${e.code()}",e)
            if(e.code() in 400..499){
                showNotficationn("Sync Failed", "Client Error")
                return Result.failure()
            }
            showNotficationn("Sync Failed", "Servor Error. Will try later")
            return Result.retry()
        }
    }
    private fun createForegroundInfo(progress: String): ForegroundInfo{
        val notification = createNotification(progress)
        return ForegroundInfo(notificationId, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
    }

    private fun createNotification(progress: String): Notification{
        return NotificationCompat.Builder(appContext, "SYC_CHANNEL_ID")
            .setContentTitle("Syncing Tasks")
            .setContentText(progress)
            .setOngoing(true)
            .build()
    }

    private fun showNotficationn(title: String, content: String){
        val finalNotification = NotificationCompat.Builder(appContext,"SYNC_CHANNEL_ID")
            .setContentTitle(title)
            .setContentText(content)
            .build()
        notificationManager.notify(notificationId,finalNotification)
    }
}