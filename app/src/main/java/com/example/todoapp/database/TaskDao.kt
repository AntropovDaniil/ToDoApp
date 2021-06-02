package com.example.todoapp.database

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTask(taskEntity: TaskEntity)

    @Query("SELECT * FROM task_table ORDER BY taskId ASC")
    fun readAllTasks(): LiveData<List<TaskEntity>>

    @Update
    suspend fun updateTask(taskEntity: TaskEntity)

    @Delete
    suspend fun deleteTask(taskEntity: TaskEntity)

    @Query("DELETE FROM task_table")
    suspend fun deleteAllTasks()

    @Query("SELECT * FROM task_table WHERE taskName LIKE :query")
    fun searchTask(query: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM task_table ORDER BY taskId ASC")
    fun getAllTasks(): List<TaskEntity>
}