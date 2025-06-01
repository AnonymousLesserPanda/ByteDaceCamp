package com.example.bytedancecamplab2

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class UserDataBaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "User.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "user_info"
        private val userInfoColumns = arrayOf("id", "userName", "password")
    }

    data class UserInfo(
        val id: Long,
        val userName: String,
        val password: String
    )

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME(
                ${userInfoColumns[0]} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${userInfoColumns[1]} TEXT NOT NULL,
                ${userInfoColumns[2]} TEXT NOT NULL
            )
        """.trimIndent()
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun addUser(userName: String, password: String): Long {
        val db: SQLiteDatabase = writableDatabase
        val values = ContentValues().apply {
            put(userInfoColumns[1], userName)
            put(userInfoColumns[2], password)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun findUserByUserName(userName: String): List<UserInfo> {
        val db: SQLiteDatabase = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_NAME,
            userInfoColumns,
            "${userInfoColumns[1]} = ?",
            arrayOf(userName),
            null, null, null
        )
        val res = mutableListOf<UserInfo>()
        try {
            val idIndex = cursor.getColumnIndex(userInfoColumns[0])
            val userNameIndex = cursor.getColumnIndex(userInfoColumns[1])
            val passwordIndex = cursor.getColumnIndex(userInfoColumns[2])

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val userName = cursor.getString(userNameIndex)
                val password = cursor.getString(passwordIndex)
                res.add(UserInfo(id, userName, password))
            }
        } catch (e: Exception) {
            Log.e("database", "username查询错误", e)
        } finally {
            cursor.close()
        }
        return res
    }
}