package com.example.todoapp.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "task_table")
@Parcelize
data class TaskEntity(
    @PrimaryKey (autoGenerate = true)
    val taskId: Long = 0,
    val taskName: String,
    val taskDescription: String = "",
    val taskDate: String = "",
    val taskTime: String = "",
    val taskPriority: String = "low_priority",
    val taskRemindFlag: Boolean = false,
    var taskDoneFlag: Boolean = false
): Parcelable
