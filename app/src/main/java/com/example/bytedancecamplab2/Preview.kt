package com.example.bytedancecamplab2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Preview : AppCompatActivity() {
    private lateinit var listRecyclerView: RecyclerView
    private lateinit var addButton: Button
    private lateinit var infoCardAdapter: InfoCardAdapter
    private val viewModel: PreviewViewModel by viewModels()

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
        infoCardAdapter = InfoCardAdapter()
        listRecyclerView.layoutManager = LinearLayoutManager(this)
        listRecyclerView.adapter = infoCardAdapter

        //观察viewModel
        viewModel.infoCardList.observe(this) { cards ->
            infoCardAdapter.submitList(cards)
        }

        addButton.setOnClickListener { addNote() }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshData(
            getSharedPreferences("userStatus", MODE_PRIVATE)
                .getLong("userId", -1L)
        )
    }

    private fun addNote() {
        val intent = Intent(this, Note::class.java)
        intent.putExtra("id", -1L)
        intent.putExtra("isNew", true)
        startActivity(intent)
    }
}