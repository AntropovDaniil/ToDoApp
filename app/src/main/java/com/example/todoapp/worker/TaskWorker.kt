package com.example.todoapp.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.todoapp.R
import com.example.todoapp.database.TaskDao
import com.example.todoapp.database.TaskDatabase
import com.example.todoapp.database.TaskEntity
import com.example.todoapp.repository.TaskRepository
import com.example.todoapp.ui.MainActivity
import com.example.todoapp.utils.taskTimeCompareMap
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.Exception
import java.util.*
import javax.inject.Inject

class TaskWorker @Inject constructor(
    @ApplicationContext context: Context,
    private val repository: TaskRepository,
    workerParams: WorkerParameters): Worker(context, workerParams) {

    private var taskList: List<TaskEntity> = emptyList()

    companion object{
        const val TASK_WORKER_TAG = "TaskWorkerDate"
        const val CHANNEL_ID = "1"
    }
    private val NOTIFICATION_ID = 0

    override fun doWork(): Result {
        return try{
            Log.d(TASK_WORKER_TAG, "Worker started")
            createNotificationChannel()


            taskList = getTaskList()
            if(taskList.isNotEmpty()) {
                checkTaskDate(taskList)
            }

            Result.success()
        }
        catch (e: Exception){
            Log.d(TASK_WORKER_TAG, "Work Failed")
            Result.failure()
        }
    }

    private fun checkTaskDate(taskList: List<TaskEntity>){
        val currentDate = Calendar.getInstance().time

        var dayOfMonth: Int
        var month: Int
        var year: Int
        var hour: Int
        var minute: Int
        var taskTime: Int

        for (task in taskList.sortedByDescending { taskTimeCompareMap(it.taskDate, it.taskTime) }){
            Log.d(TASK_WORKER_TAG, "TaskName: ${task.taskName} taskDate: ${task.taskDate}")
            if (task.taskDate.isEmpty() || task.taskDoneFlag || !task.taskRemindFlag) continue

            if (task.taskDate.isNotEmpty() && task.taskTime.isNotEmpty()) {
                dayOfMonth = task.taskDate.substringBefore(".").toInt()
                month = task.taskDate.substringAfter(".").substringBefore(".").toInt()
                year = task.taskDate.substringAfterLast(".").toInt() - 1900
                hour = task.taskTime.substringBefore(":").toInt()
                minute = task.taskTime.substringAfter(":").toInt()
                val taskDate = Date(year, month, dayOfMonth, hour, minute)

                if(taskDate>currentDate){
                    taskTime = convertMillisecondsToHours(taskDate.time - currentDate.time)
                    if (taskTime<24)
                        createNotificationWithTime(task.taskName, taskTime)
                    else createNotificationWithDate(task.taskName, taskTime)
                }
            }
            if (task.taskDate.isNotEmpty()){
                dayOfMonth = task.taskDate.substringBefore(".").toInt()
                month = task.taskDate.substringAfter(".").substringBefore(".").toInt()
                year = task.taskDate.substringAfterLast(".").toInt() - 1900
                val taskDate = Date(year, month, dayOfMonth)
                if (taskDate>currentDate){
                    taskTime = convertMillisecondsToHours(taskDate.time - currentDate.time)
                    createNotificationWithDate(task.taskName, taskTime)
                }
            }
        }

    }

    private fun getTaskList(): List<TaskEntity>{
        var resultList: List<TaskEntity> = repository.getTaskForWorker()
        return resultList
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "TaskNotification"
            val descriptionText = "Notification for task"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            channel.setSound(null, null)
            // Register the channel with the system
            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotificationWithTime(taskName: String, dateDiff: Int){
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(taskName)
            .setContentText("You have task in $dateDiff hour(s)")
            .setSmallIcon(R.drawable.ic_star_icon)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationWithDate(taskName: String, dateDiff: Int){
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)

        val message = if (dateDiff>24) "Your have task in ${(dateDiff/24)} days" else "You have nearest task!"
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(taskName)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_star_icon)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun convertMillisecondsToHours(milliseconds: Long): Int{
        var result: Int = (milliseconds/1000/60/60).toInt()
        return result
    }
}