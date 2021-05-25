package com.example.todoapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [TaskEntity::class], version = 6, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TaskDatabase: RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object{
        @Volatile
        private var DATABASE_INSTANCE: TaskDatabase? = null

        fun getDatabase(context: Context): TaskDatabase{
            val tempInstance = DATABASE_INSTANCE
            if (tempInstance != null)
                return tempInstance
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                DATABASE_INSTANCE = instance
                return instance
            }
        }
    }
}