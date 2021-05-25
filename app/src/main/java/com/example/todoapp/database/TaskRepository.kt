package com.example.todoapp.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.todoapp.mappers.TaskMapper
import com.example.todoapp.models.Task

class TaskRepository(private val taskDao: TaskDao) {

    val readAllTasks: LiveData<List<TaskEntity>> = taskDao.readAllTasks()


    suspend fun addTask(task: TaskEntity){
        taskDao.addTask(task)  //(TaskMapper.mapTaskToTaskEntity(task))
    }

    suspend fun updateTask(task: TaskEntity){
        taskDao.updateTask(task) //(TaskMapper.mapTaskToTaskEntity(task))
    }

    suspend fun deleteTask(task: TaskEntity){
        taskDao.deleteTask(task) //(TaskMapper.mapTaskToTaskEntity(task))
    }

    suspend fun deleteAllTasks(){
        taskDao.deleteAllTasks()
    }

}