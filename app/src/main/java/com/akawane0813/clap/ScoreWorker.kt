package com.akawane0813.clap

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters



const val KEY_SCORE = "com.akawane0813.clap.SCORE"
const val NOTIFICATION_ID = 0
const val CHANNEL_ID_TIMER = "channel_timer"

class TimerWorker(context: Context, parameters: WorkerParameters) :
    Worker(context, parameters) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override fun doWork(): Result {

        // Get score from MainActivity
        val score = inputData.getString(KEY_SCORE)


        // Create notification channel for all notifications
        createTimerNotificationChannel()

        createTimerNotification(score.toString())

        return Result.success()
    }

    private fun createTimerNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = applicationContext.getString(R.string.channel_name)
            val description = applicationContext.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID_TIMER, name, importance)
            channel.description = description

            // Register channel with system
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createTimerNotification(text: String) {
        // Create notification with various properties
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID_TIMER)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        // Post notification
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}