package com.example.bytedancecamplab2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

class Preview : AppCompatActivity() {
    private lateinit var listRecyclerView: RecyclerView
    private lateinit var addButton: Button
    private lateinit var infoCardAdapter: InfoCardAdapter
    private val infoCard = NoteDataBaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_preview)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences = getSharedPreferences("userStatus", MODE_PRIVATE)
        val userId = sharedPreferences.getLong("userId", -1L)
        val infoCardList = infoCard.findInfoCardByUserId(userId)

        //组件绑定
        listRecyclerView = findViewById<RecyclerView>(R.id.list_view)
        addButton = findViewById<Button>(R.id.add_button)
        infoCardAdapter = InfoCardAdapter(infoCardList)

        addButton.setOnClickListener { addNote() }
    }

    private fun addNote() {
        val intent = Intent(this, Note::class.java)
        intent.putExtra("id", -1L)
        intent.putExtra("isNew", true)
        startActivity(intent)
    }
}