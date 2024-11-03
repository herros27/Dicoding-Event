package com.example.submission_1_fundamental_android.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.submission_1_fundamental_android.R
import com.example.submission_1_fundamental_android.di.Injection
import com.example.submission_1_fundamental_android.repository.EventRepository
import java.text.SimpleDateFormat
import java.util.Locale

class Worker(context: Context, worker: WorkerParameters) : CoroutineWorker(context, worker) {
    private val repository: EventRepository = Injection.provideRepository(context)


    override suspend fun doWork(): Result {
        return try {
            val nearEvent = repository.getNearestEvent()
            val event = nearEvent?.listEvents?.firstOrNull()
            if (event!= null){
                val formattedDate = dateEventFormat(event.beginTime)
                val notifTitle = "Event Reminder : $formattedDate"
                showNotification(notifTitle, event.name, event.link)
                Result.success()
            }else{
                Result.failure()
            }
        }catch (e: Exception){
            Result.failure()
        }
    }

    private fun showNotification(title: String, description: String, link: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(link)
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        createNotificationChannel()

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_date)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(description))
            .setAutoCancel(true)

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for event reminders"
            }

            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun dateEventFormat(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = inputFormat.parse(dateString)

        val outputFormat = SimpleDateFormat("d MMM, HH:mm", Locale.getDefault())
        return date?.let { outputFormat.format(it) } ?: "Unknown date"
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "event_reminder_channel"
        const val CHANNEL_NAME = "Event Reminder Channel"
    }
}