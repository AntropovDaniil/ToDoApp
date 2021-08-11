package com.example.todoapp.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.todoapp.database.TaskEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

class AlarmService @Inject constructor(
    @ApplicationContext context: Context,
) {

    companion object{
        val CHANNEL_ID = "1"
    }

    private val alarmManager: AlarmManager? = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?

    private val launchIntent = Intent(context, AlarmReceiver::class.java)
    private val launchPendingIntent = PendingIntent.getBroadcast(
        context,
        AlarmReceiver.ALARM_REQUEST_CODE,
        launchIntent,
        AlarmReceiver.ALARM_FLAG)

    private val reminderIntent = Intent(context, ReminderReceiver::class.java)
    private val reminderPendingIntent = PendingIntent.getBroadcast(
        context,
        ReminderReceiver.REMINDER_REQUEST_CODE,
        reminderIntent,
        ReminderReceiver.REMINDER_FLAG
    )


    fun createTaskReminder(task: TaskEntity){
        reminderIntent.putExtra(ReminderReceiver.TASK_NAME_KEY, task.taskName)
        alarmManager?.let {
            it.setExact(
                AlarmManager.RTC_WAKEUP,
                getTaskTimeInMills(task),
                reminderPendingIntent
            )
        }
    }


    fun launchAlarm(){
        alarmManager?.let {
            it.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                getAlarmTime().timeInMillis,
                AlarmManager.INTERVAL_DAY,
                launchPendingIntent
            )

        }
    }

    private fun getAlarmTime(): Calendar{
        val calendar = Calendar.getInstance()
        calendar.apply {
            set(Calendar.DAY_OF_YEAR, this.get(Calendar.DAY_OF_YEAR))
            set(Calendar.HOUR_OF_DAY, 20) //20
        }
        return calendar
    }

    private fun getTaskTimeInMills(task: TaskEntity): Long{
        val calendar = Calendar.getInstance()
        val taskYear = task.taskDate.substringAfterLast(".").toInt() - 1900
        val taskMonth = task.taskDate.substringAfter(".").substringBefore(".").toInt()
        val taskDay = task.taskDate.substringBefore(".").toInt()
        calendar.apply {
            set(Calendar.YEAR, taskYear)
            set(Calendar.MONTH, taskMonth)
            set(Calendar.DAY_OF_MONTH, taskDay)
        }
        if (task.taskTime != "00:00" || task.taskDate.isNotEmpty()){
            val taskHour = task.taskTime.substringBefore(":").toInt()
            val taskMinute = task.taskTime.substringAfter(":").toInt()
            calendar.apply {
                set(Calendar.HOUR_OF_DAY, taskHour)
                set(Calendar.MINUTE, taskMinute - 10)
            }
            reminderIntent.putExtra(ReminderReceiver.TASK_TIME_KEY, task.taskTime)
        }

        return calendar.timeInMillis
    }


}