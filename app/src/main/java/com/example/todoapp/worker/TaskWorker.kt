package com.example.todoapp.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.todoapp.R
import com.example.todoapp.database.TaskDatabase
import com.example.todoapp.database.TaskEntity
import com.example.todoapp.database.TaskRepository
import java.lang.Exception
import java.util.*

class TaskWorker(context: Context, workerParams: WorkerParameters): Worker(context, workerParams) {

    private val repository: TaskRepository
    private var taskList: List<TaskEntity> = emptyList()

    init {
        val taskDao = TaskDatabase.getDatabase(context).taskDao()
        repository = TaskRepository(taskDao)
    }

    companion object{
        const val TASK_WORKER_TAG = "TaskWorkerDate"
        const val CHANNEL_ID = "1"
    }
    private val NOTIFICATION_ID = 0

    override fun doWork(): Result {
        return try{
            Log.d(TASK_WORKER_TAG, "Worker started")

            taskList = getTaskList()
            Log.d(TASK_WORKER_TAG, "TaskList from doWork: ${taskList}")
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

        for (task in taskList){
            Log.d(TASK_WORKER_TAG, "TaskName: ${task.taskName} taskDate: ${task.taskDate}")
            if (task.taskDate.isNotEmpty()) {
                val dayOfMonth = task.taskDate.substringBefore(".").toInt()
                val month = task.taskDate.substringAfter(".").substringBefore(".").toInt()
                val year = task.taskDate.substringAfterLast(".").toInt() - 1900
                val taskDate = Date(year, month, dayOfMonth)
                Log.d(
                    TASK_WORKER_TAG,
                    "CustomTime: ${Date(year, month, dayOfMonth)}"
                )

                if(taskDate>currentDate){
                    createNotification()
                }
            }
        }

    }

    private fun getTaskList(): List<TaskEntity>{
        var resultList: List<TaskEntity> = repository.getTaskForWorker()
        return resultList
    }

    private fun createNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "TaskNotification"
            val descriptionText = "Notification for task"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Task notification")
            .setContentText("You have next task")
            .setSmallIcon(R.drawable.ic_star_icon)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()


        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}