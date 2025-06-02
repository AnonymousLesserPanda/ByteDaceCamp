package com.example.bytedancecamplab2

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.DialogTitle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.min

class Note() : AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var noteEditText: EditText
    private lateinit var fileName: String
    private var isNew = true
    private var id = -1L
    private val infoCard = NoteDataBaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_note)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        isNew = intent.getBooleanExtra("isNew", true)
        id = intent.getLongExtra("id", -1L)
        fileName = "${id}.json"

        //绑定组件
        titleEditText = findViewById<EditText>(R.id.title_input)
        noteEditText = findViewById<EditText>(R.id.note_input)

        if (!isNew) {
            loadFile()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        save()
    }

    private fun loadFile() {
        try {
            val file = File(filesDir, fileName)
            if (!file.exists()) {
                return
            }

            val content = file.readText(Charsets.UTF_8)

            val jsonObject = JSONObject(content)
            val title = jsonObject.optString("title", "")
            val note = jsonObject.optString("note", "")

            titleEditText.setText(title)
            noteEditText.setText(note)

        } catch (e: FileNotFoundException) {
            Log.e("Note", "文件不存在", e)
        } catch (e: IOException) {
            Log.e("Note", "IO异常", e)
        } catch (e: JSONException) {
            Log.e("Note", "解析错误", e)
        }
    }

    private fun saveFile() {
        try {
            val jsonObject = JSONObject()
            jsonObject.put("title", titleEditText.text.toString())
            jsonObject.put("note", noteEditText.text.toString())

            val file = File(filesDir, fileName)

            FileOutputStream(file).use { fos ->
                fos.write(jsonObject.toString().toByteArray(Charsets.UTF_8))
            }

        } catch (e: JSONException) {
            Log.e("Note", "键值错误", e)
        } catch (e: FileNotFoundException) {
            Log.e("Note", "路径错误", e)
        } catch (e: IOException) {
            Log.e("Note", "写入异常", e)
        } catch (e: SecurityException) {
            Log.e("Note", "权限异常", e)
        }
    }

    private fun save() {
        val endIndex = min(20, noteEditText.text.length)
        if (titleEditText.text.isEmpty() && noteEditText.text.isEmpty()) {
            return
        } else if (titleEditText.text.isEmpty()) {
            titleEditText.setText(noteEditText.text.substring(0, endIndex))
        }
        val sharedPreferences = getSharedPreferences("userStatus", MODE_PRIVATE)
        val userId = sharedPreferences.getLong("userId", -1L)
        val title = titleEditText.text.toString()
        var brief = noteEditText.text.substring(0, endIndex)
        if (endIndex != noteEditText.text.length) {
            brief += "... ..."
        }
        saveFile()
        if (isNew) {
            infoCard.addInfoCard(userId, title, brief)
        } else {
            infoCard.updateInfoCard(id, title, brief)
        }
    }
}