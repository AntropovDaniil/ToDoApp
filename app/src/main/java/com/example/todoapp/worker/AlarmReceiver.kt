package com.example.todoapp.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.todoapp.R
import com.example.todoapp.database.TaskEntity
import com.example.todoapp.repository.TaskRepository
import com.example.todoapp.ui.MainActivity
import com.example.todoapp.utils.taskDateCompare
import java.util.*
import javax.inject.Inject

class AlarmReceiver @Inject constructor(
    private val repository: TaskRepository
): BroadcastReceiver() {

    @Inject lateinit var alarmService: AlarmService

    companion object{
        val ALARM_REQUEST_CODE = 0
        val ALARM_FLAG = 10
    }
    private val NOTIFICATION_ID = 0

    private var receiverContext: Context? = null

    private var taskList = emptyList<TaskEntity>()

    override fun onReceive(context: Context?, intent: Intent?) {
        receiverContext = context
        taskList = getTaskList()

        if(taskList.isNotEmpty()) {
            checkTaskDate(taskList)
        }

        context?.let {
            createNotificationChannel()
        }
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "TaskNotification"
            val descriptionText = "Notification for task"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(AlarmService.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            channel.setSound(null, null)
            val manager = receiverContext?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createEveningNotification(taskCount: Int){
        val intent = Intent(receiverContext, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(receiverContext, 0, intent, 0)

        val message = "You have $taskCount tomorrow"
        //if (dateDiff>24) "Your have task in ${(dateDiff/24)} days" else "You have nearest task!"
        val notification = NotificationCompat.Builder(receiverContext!!, AlarmService.CHANNEL_ID)
            .setContentTitle("TodoApp")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_star_icon)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = NotificationManagerCompat.from(receiverContext!!)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun getTaskList(): List<TaskEntity>{
        var resultList: List<TaskEntity> = repository.getTaskForWorker()
        return resultList
    }

    private fun checkTaskDate(taskList: List<TaskEntity>){
        var tomorrowTaskCount = 0

        for (task in taskDateCompare(taskList).sortedBy { it.taskDoneFlag }){
            if (task.taskDate.isEmpty() || task.taskDoneFlag || !task.taskRemindFlag) continue
            if (task.taskDate.isNotEmpty() && task.taskTime.isNotEmpty()) {
                val taskDate = getDateFromString(task.taskDate)
                if (taskDate.day + 1 == Calendar.DAY_OF_MONTH && taskDate.year == Calendar.YEAR) {
                    tomorrowTaskCount++
                    createReminder(task)
                }

            }
            if (tomorrowTaskCount!=0)
                createEveningNotification(tomorrowTaskCount)
        }
    }

//    private fun getDateAndTimeFromString(taskDate: String, taskTime: String): Date{
//        val dayOfMonth = taskDate.substringBefore(".").toInt()
//        val month = taskDate.substringAfter(".").substringBefore(".").toInt()
//        val year = taskDate.substringAfterLast(".").toInt() - 1900
//        val hour = taskTime.substringBefore(":").toInt()
//        val minute = taskTime.substringAfter(":").toInt()
//        return Date(year, month, dayOfMonth, hour, minute)
//    }

    private fun getDateFromString(taskDate: String): Date{
        val dayOfMonth = taskDate.substringBefore(".").toInt()
        val month = taskDate.substringAfter(".").substringBefore(".").toInt()
        val year = taskDate.substringAfterLast(".").toInt() - 1900
        return Date(year, month, dayOfMonth)
    }

    private fun createReminder(task: TaskEntity){
        alarmService.createTaskReminder(task)
    }

}