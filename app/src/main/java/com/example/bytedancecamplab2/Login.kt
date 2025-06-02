package com.example.bytedancecamplab2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.content.edit

class Login : AppCompatActivity() {
    private var userName = ""
    private var password = ""
    private val userInfo = UserDataBaseHelper(this)

    private lateinit var userNameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //绑定组件
        userNameEditText = findViewById<EditText>(R.id.user_name_input)
        passwordEditText = findViewById<EditText>(R.id.password_input)
        loginButton = findViewById<Button>(R.id.login_button)
        registerButton = findViewById<Button>(R.id.register_button)

        loginButton.setOnClickListener { login() }
        registerButton.setOnClickListener { register() }
    }

    private fun login() {
        userName = userNameEditText.text.toString()
        password = passwordEditText.text.toString()
        val res = userInfo.findUserByUserName(userName)
        if (res.isEmpty()) {
            show("用户不存在")
            return
        }
        val user = res[0]
        if (password != user.password) {
            show("用户名或密码错误")
            return
        }
        val sharedPreferences = getSharedPreferences("userStatus", MODE_PRIVATE)
        sharedPreferences.edit {
            putBoolean("pass", true)
            putLong("userId", user.id)
        }
        // TODO:跳转到后续界面
    }

    private fun register() {
        val intent = Intent(this, Register::class.java)
        startActivity(intent)
    }

    private fun show(message: String) {
        Log.i("Login", message)
    }

}