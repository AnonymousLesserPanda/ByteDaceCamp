package com.example.bytedancecamplab1

import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        private const val CHANNEL_ID = "alarm_channel"
        private const val NOTIFICATION_ID = 1
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        createNotificationChannel(context)
        showAlarmNotification(context)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for alarm alerts"
                enableLights(true)
                lightColor = context.getColor(R.color.holo_red_dark)
                enableVibration(true)
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showAlarmNotification(context: Context) {
        val channelId = CHANNEL_ID
        val notificationManager = NotificationManagerCompat.from(context)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_dialog_alert)
            .setContentTitle("闹钟")
            .setContentText("时间到！")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}