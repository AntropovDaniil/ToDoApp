package com.example.todoapp.utils

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
    else if (date.isNotEmpty() && time.isEmpty()){
        result = Date(
            date.substringAfterLast(".").toInt(),
            date.substringAfter(".").substringBefore(".").toInt(),
            date.substringBefore(".").toInt()
        )
    }
    return result
}