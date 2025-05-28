package com.example.bytedancecamplab1

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class Alarm : AppCompatActivity() {

    private lateinit var alarmManager: AlarmManager
    private lateinit var timePicker: TimePicker
    private lateinit var buttonSetAlarm: Button
    private lateinit var textViewAlarmTime: TextView
    private var alarmRequestCode = 1000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_alarm)

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
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
        // 为什么要写class.java
        val alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(
                this,
                alarmRequestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        try {
            alarmManager.setExact(
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
        val timeFormat = if (hour < 12) "AM" else "PM"
        val displayHour = if (hour > 12) hour - 12 else hour
        textViewAlarmTime.text =
            "闹钟时间：${displayHour}:${minute.toString().padStart(2, '0')} $timeFormat"
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
        cancelAlarm()
    }
}