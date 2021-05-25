package com.example.todoapp.mappers

import androidx.lifecycle.LiveData
import com.example.todoapp.database.TaskEntity
import com.example.todoapp.models.Task
import com.example.todoapp.models.TaskPriority

object TaskMapper {

    fun mapTaskToTaskEntity(task: Task): TaskEntity{
        val priority = when(task.taskPriority){
            TaskPriority.LOW_PRIORITY -> "low_priority"
            TaskPriority.MEDIUM_PRIORITY -> "medium_priority"
            TaskPriority.HIGH_PRIORITY -> "high_priority"
            else -> "low_priority"
        }
        return TaskEntity(
            0,
            task.taskName,
            task.taskDescription,
            task.taskDate,
            task.taskTime,
            priority,
            task.taskRemindFlag
        )
    }

    fun mapTaskEntityToTask(taskEntity: TaskEntity): Task{
        var priority: TaskPriority = TaskPriority.LOW_PRIORITY
        when(taskEntity.taskPriority){
            "low_priority" -> priority = TaskPriority.LOW_PRIORITY
            "medium_priority" -> priority = TaskPriority.MEDIUM_PRIORITY
            "high_priority" -> priority = TaskPriority.HIGH_PRIORITY
        }
        return Task(
            taskEntity.taskName,
            taskEntity.taskDescription,
            taskEntity.taskDate,
            taskEntity.taskTime,
            priority,
            taskEntity.taskRemindFlag
        )
    }

    fun mapTaskListToTaskEntityList(taskList: List<Task>): List<TaskEntity> {
        val taskEntityList = mutableListOf<TaskEntity>()
        for (task in taskList){
            taskEntityList.add(mapTaskToTaskEntity(task))
        }
        return taskEntityList
    }

    fun mapTaskEntityListToTaskList(taskEntityList: List<TaskEntity>): List<Task>{
        val taskList = mutableListOf<Task>()
        for (taskEntity in taskEntityList){
            taskList.add(mapTaskEntityToTask(taskEntity))
        }
        return taskList
    }
}