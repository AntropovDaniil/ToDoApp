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
import com.example.todoapp.ui.MainActivity

class ReminderReceiver: BroadcastReceiver() {

    companion object{
        val REMINDER_REQUEST_CODE = 1
        val REMINDER_FLAG = 11
        val TASK_NAME_KEY = "TASK_NAME_KEY"
        val TASK_TIME_KEY = "TASK_TIME_KEY"
        val NOTIFICATION_ID = 1
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            createNotificationChannel(it)

            intent?.let {
                val taskName = it.getStringExtra(TASK_NAME_KEY)
                val taskTime = it.getStringExtra(TASK_TIME_KEY)
                createTaskNotification(context, taskName, taskTime)
            }
        }
    }

    private fun createNotificationChannel(context: Context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "TaskNotification"
            val descriptionText = "Notification for task"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(AlarmService.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            channel.setSound(null, null)
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createTaskNotification(context: Context, taskName: String?, taskTime: String?){
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val message = "You have task:$taskName at $taskTime"
        val notification = NotificationCompat.Builder(context, AlarmService.CHANNEL_ID)
            .setContentTitle("TodoApp")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_star_icon)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}