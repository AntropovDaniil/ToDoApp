package com.example.todoapp.utils

import com.example.todoapp.database.TaskEntity
import java.util.*

fun taskTimeCompareMap(date: String, time: String): Date {
    var result = Date()
    if (date.isNotEmpty() && time.isNotEmpty()){
        result = Date(
            date.substringAfterLast(".").toInt(),
            date.substringAfter(".").substringBefore(".").toInt(),
            date.substringBefore(".").toInt(),
            time.substringBefore(":").toInt(),
            time.substringAfter(":").toInt()
        )
    }
    else if (date.isNotEmpty() ){
        result = Date(
            date.substringAfterLast(".").toInt(),
            date.substringAfter(".").substringBefore(".").toInt(),
            date.substringBefore(".").toInt(),
            0,
            0
        )
    }
    return result
}

fun taskDateCompare(taskList: List<TaskEntity>): List<TaskEntity>{
    val resultList = mutableListOf<TaskEntity>()
    var taskListWithDate = mutableListOf<TaskEntity>()
    val taskListWithoutDate = mutableListOf<TaskEntity>()
    for (task in taskList){
        if (task.taskDate.isNotEmpty())
            taskListWithDate.add(task)
        else
            taskListWithoutDate.add(task)
    }
    taskListWithDate = taskListWithDate.sortedBy{taskTimeCompareMap(it.taskDate, it.taskTime)}.toMutableList()
    resultList.addAll(taskListWithDate)
    resultList.addAll(taskListWithoutDate)

    return resultList
}