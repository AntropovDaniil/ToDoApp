package com.example.todoapp.di

import android.content.Context
import androidx.room.Room
import com.example.todoapp.database.TaskDao
import com.example.todoapp.database.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    fun provideTaskDao(taskDatabase: TaskDatabase): TaskDao{
        return taskDatabase.taskDao()
    }

    @Provides
    @Singleton
    fun provideTaskDatabase(@ApplicationContext appContext: Context): TaskDatabase{
        return Room.databaseBuilder(
            appContext.applicationContext,
            TaskDatabase::class.java,
            "task_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}