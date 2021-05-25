package com.example.todoapp.database

import androidx.room.TypeConverter
import com.example.todoapp.enums.TaskPriority

class Converters {

    @TypeConverter
    fun fromTaskPriority(priorityValue: TaskPriority): String{
        if (priorityValue == TaskPriority.HIGH_PRIORITY) return "high_priority"
        if (priorityValue == TaskPriority.MEDIUM_PRIORITY) return "medium_priority"
        return "low_priority"
    }

    @TypeConverter
    fun fromString(value: String): TaskPriority{
        if (value == "high_priority") return TaskPriority.HIGH_PRIORITY
        if (value == "medium_priority") return TaskPriority.MEDIUM_PRIORITY
        return TaskPriority.LOW_PRIORITY
    }
}