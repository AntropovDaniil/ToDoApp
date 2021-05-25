package com.example.todoapp.enums

enum class TaskPriority: Comparator<TaskPriority> {
    LOW_PRIORITY, MEDIUM_PRIORITY, HIGH_PRIORITY;

    override fun compare(o1: TaskPriority?, o2: TaskPriority?): Int {
        if (o1 == LOW_PRIORITY && o2 == MEDIUM_PRIORITY) return -1
        if (o1 == MEDIUM_PRIORITY && o2 == HIGH_PRIORITY) return -1
        if (o1 == LOW_PRIORITY && o2 == HIGH_PRIORITY) return -1
        if (o1 == HIGH_PRIORITY && o2 == LOW_PRIORITY) return 1
        if (o1 == HIGH_PRIORITY && o2 == MEDIUM_PRIORITY) return 1
        if (o1 == MEDIUM_PRIORITY && o2 == LOW_PRIORITY) return 1
        return 0
    }
}