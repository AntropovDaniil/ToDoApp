package com.example.todoapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    val taskName: String,
    val taskDescription: String,
    val taskDate: String,
    val taskTime: String,
    val taskPriority: TaskPriority,
    val taskRemindFlag: Boolean
): Parcelable


enum class TaskPriority{
    LOW_PRIORITY, MEDIUM_PRIORITY, HIGH_PRIORITY
}

