package com.example.todoapp.di

import com.example.todoapp.adapters.TaskListAdapter
import com.example.todoapp.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class AdapterModule {

    @Provides
    fun provideTaskListAdapter(taskRepository: TaskRepository): TaskListAdapter{
        return TaskListAdapter(taskRepository)
    }
}