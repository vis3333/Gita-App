package com.example.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.GitaPreferences
import com.example.data.GitaRepository
import java.util.Calendar

class DailyGitaNotificationReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "daily_gita_teachings_channel"
        const val NOTIFICATION_ID = 1008
        const val ACTION_DAILY_NOTIFICATION = "com.aistudio.gitadaily.ACTION_DAILY_GITA_NOTIFICATION"
        const val EXTRA_VERSE_ID = "extra_verse_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val prefs = GitaPreferences(context)

        if (Intent.ACTION_BOOT_COMPLETED == action) {
            // Re-schedule alarm on boot if enabled
            if (prefs.isNotificationEnabled) {
                NotificationScheduler.scheduleDailyNotification(
                    context,
                    prefs.notificationHour,
                    prefs.notificationMinute
                )
            }
            return
        }

        // Show today's verse notification
        showNotification(context)

        // Reschedule for next day if enabled
        if (prefs.isNotificationEnabled) {
            NotificationScheduler.scheduleDailyNotification(
                context,
                prefs.notificationHour,
                prefs.notificationMinute
            )
        }
    }

    fun showNotification(context: Context) {
        createNotificationChannel(context)

        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val dailyVerse = GitaRepository.getDailyVerse(dayOfYear)

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_VERSE_ID, dailyVerse.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            dailyVerse.id.hashCode(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationTitle = "Daily Gita Teaching: ${dailyVerse.verseReference}"
        val notificationSummary = "${dailyVerse.coreTeaching}\n\"${dailyVerse.englishTranslation}\""

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(notificationTitle)
            .setContentText(dailyVerse.coreTeaching)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle("Daily Gita Wisdom • ${dailyVerse.verseReference}")
                    .bigText(notificationSummary)
                    .setSummaryText(dailyVerse.chapterName)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        } catch (_: SecurityException) {
            // Permission POST_NOTIFICATIONS may not be granted yet
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Daily Gita Teachings"
            val descriptionText = "Delivers your daily Bhagavad Gita verse, translation, and life wisdom."
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
