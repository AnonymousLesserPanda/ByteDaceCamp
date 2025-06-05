package com.example.bytedancecamplab2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates

class Preview : AppCompatActivity() {
    private lateinit var listRecyclerView: RecyclerView
    private lateinit var addButton: Button
    private lateinit var infoCardAdapter: InfoCardAdapter
    private val viewModel: PreviewViewModel by viewModels()
    private var userId = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_preview)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //组件绑定
        listRecyclerView = findViewById(R.id.list_view)
        addButton = findViewById(R.id.add_button)

        //初始化
        userId = getSharedPreferences("userStatus", MODE_PRIVATE)
            .getLong("userId", -1L)
        infoCardAdapter = InfoCardAdapter()
        listRecyclerView.layoutManager = LinearLayoutManager(this)
        listRecyclerView.adapter = infoCardAdapter

        //注册广播
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(refreshReceiver, IntentFilter("com.example.ACTION_REFRESH_PREVIEW"))


        addButton.setOnClickListener { addNote() }
    }

    override fun onResume() {
        super.onResume()
        //观察viewModel
        viewModel.infoCardList.observe(this) { cards ->
            infoCardAdapter.submitList(cards)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(refreshReceiver)
    }

    private val refreshReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.refreshData(userId)
        }
    }

    private fun addNote() {
        val intent = Intent(this, Note::class.java)
        intent.putExtra("id", -1L)
        intent.putExtra("isNew", true)
        startActivity(intent)
    }
}