package com.example.todoapp.database

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import org.w3c.dom.Entity

class TaskRepository(private val taskDao: TaskDao) {

    val readAllTasks: LiveData<List<TaskEntity>> = taskDao.readAllTasks()

    suspend fun addTask(task: TaskEntity){
        taskDao.addTask(task)  //(TaskMapper.mapTaskToTaskEntity(task))
    }

    suspend fun updateTask(task: TaskEntity){
        taskDao.updateTask(task) //(TaskMapper.mapTaskToTaskEntity(task))
    }

    suspend fun deleteTask(task: TaskEntity){
        taskDao.deleteTask(task)
    }

    suspend fun deleteAllTasks(){
        taskDao.deleteAllTasks()
    }

    fun searchTask(query: String): Flow<List<TaskEntity>> {
        return taskDao.searchTask(query)
    }

    fun getTaskForWorker(): List<TaskEntity>{
        return taskDao.getAllTasks()
    }

}