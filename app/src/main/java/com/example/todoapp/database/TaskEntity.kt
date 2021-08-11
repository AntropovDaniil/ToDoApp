package com.example.todoapp.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todoapp.enums.TaskPriority
import kotlinx.parcelize.Parcelize

@Entity(tableName = "task_table")
@Parcelize
data class TaskEntity(
    @PrimaryKey (autoGenerate = true)
    val taskId: Int = 0,
    val taskName: String,
    val taskDescription: String = "",
    val taskDate: String = "",
    val taskTime: String = "00:00",
    val taskPriority: TaskPriority = TaskPriority.LOW_PRIORITY,
    val taskRemindFlag: Boolean = false,
    var taskDoneFlag: Boolean = false
): Parcelable
