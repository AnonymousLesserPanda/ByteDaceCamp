package com.example.bytedancecamplab2

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Register : AppCompatActivity() {
    private var userName = ""
    private var password = ""
    private val userInfo = UserDataBaseHelper(this)

    private lateinit var userNameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //绑定组件
        userNameEditText = findViewById<EditText>(R.id.user_name_input)
        passwordEditText = findViewById<EditText>(R.id.password_input)
        submitButton = findViewById<Button>(R.id.submit_button)
        cancelButton = findViewById<Button>(R.id.cancel_button)

        submitButton.setOnClickListener { submit() }
        cancelButton.setOnClickListener { cancel() }
    }

    private fun submit() {
        userName = userNameEditText.text.toString()
        password = passwordEditText.text.toString()
        if (!userInfo.findUserByUserName(userName).isEmpty()) {
            show("用户名已存在")
            return
        }
        val res = userInfo.addUser(userName, password)
        if (res == -1L) {
            show("注册失败")
        } else {
            show("注册成功")
        }
    }

    private fun cancel() {
        userNameEditText.setText("")
        passwordEditText.setText("")
    }

    private fun show(message: String) {
        Log.i("Register", message)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}