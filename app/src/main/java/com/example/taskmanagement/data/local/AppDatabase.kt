package com.example.taskmanagement.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.taskmanagement.data.local.dao.TaskDao
import com.example.taskmanagement.data.local.models.Task
import com.example.taskmanagement.data.local.type_converters.DateTypeConverter

@Database(entities = [Task::class], version = 1, exportSchema = false)//specifiramo entitete koje cemo imati
@TypeConverters(DateTypeConverter::class)
abstract class AppDatabase: RoomDatabase {
    abstract fun taskDao(): TaskDao

    companion object{
        @Volatile//ovo znaci da je ona promenljiva
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase{ //kreira i vraca bazu podataka
            return INSTANCE ?: synchronized(this){ //zakljucavamo sinhronizaciju da bi izbegli probleme sa vise niti,
                //zakljucavamo da se ne poziva vise od jednom
                val instance = Room.databaseBuilder(
                    context = context.applicationContext,
                    AppDatabase::class.java,
                    name = "task_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}