package com.example.bytedancecamplab1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    private lateinit var clockView: ClockView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化时钟视图
        clockView = findViewById(R.id.clockView)

        // 跳转到闹钟
        val buttonToAlarm = findViewById<Button>(R.id.buttonToAlarm)
        buttonToAlarm.setOnClickListener {
            val  intent = Intent(this,Alarm::class.java)
            startActivity(intent)
        }
    }
}