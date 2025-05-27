package com.example.bytedancecamplab1

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        AlertDialog.Builder(context)
            .setTitle("闹钟")
            .setMessage("时间到！")
            .setPositiveButton("关闭") { dialog, _ -> dialog.dismiss() }
            .setCancelable(false)
            .show()
    }
}