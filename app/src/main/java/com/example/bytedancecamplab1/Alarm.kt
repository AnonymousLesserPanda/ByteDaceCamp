package com.example.bytedancecamplab1

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts

class Alarm : AppCompatActivity() {

    private lateinit var alarmManager: AlarmManager
    private lateinit var timePicker: TimePicker
    private lateinit var buttonSetAlarm: Button
    private lateinit var textViewAlarmTime: TextView
    private var alarmRequestCode = 1001


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_alarm)

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        // 申请精确闹钟权限
        if (!alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            intent.data = "package:${packageName}".toUri()
            startActivity(intent)
        }
        //申请应用通知权限
        val notificationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {}
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }

        timePicker = findViewById(R.id.timePicker)
        timePicker.setIs24HourView(true)
        buttonSetAlarm = findViewById(R.id.buttonSetAlarm)
        textViewAlarmTime = findViewById(R.id.textViewAlarmTime)

        timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            updateAlarmTimeDisplay(
                hourOfDay,
                minute
            )
        }

        buttonSetAlarm.setOnClickListener { setAlarm() }
    }

    private fun setAlarm() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, timePicker.hour)
            set(Calendar.MINUTE, timePicker.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(
                this,
                alarmRequestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                alarmIntent
            )
        }catch (e: SecurityException){
            Log.e("Alarm","未获得精确闹钟权限",e)
        }

        Toast.makeText(this, "闹钟已设置", Toast.LENGTH_SHORT).show()
    }

    private fun updateAlarmTimeDisplay(hour: Int, minute: Int) {
        val displayHour = hour.toString().padStart(2, '0')
        val displayMinute = minute.toString().padStart(2, '0')
        textViewAlarmTime.text = "闹钟时间：$displayHour:$displayMinute"
    }

    private fun cancelAlarm() {
        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            alarmRequestCode,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Toast.makeText(this, "闹钟已取消！", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}