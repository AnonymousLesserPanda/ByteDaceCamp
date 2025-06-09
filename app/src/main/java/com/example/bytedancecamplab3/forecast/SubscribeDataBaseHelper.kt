package com.example.bytedancecamplab3.forecast

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class SubscribeDataBaseHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {
    companion object {
        private const val DATABASE_NAME = "City.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "subscribe_list"
        private val SubscribeColumns = arrayOf("cityCode", "city")
    }

    data class Subscribe(
        val cityCode: String, val city: String
    )

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
        CREATE TABLE ${TABLE_NAME}(
        ${SubscribeColumns[0]} TEXT NOT NULL,
        ${SubscribeColumns[1]} TEXT NOT NULL,
       PRIMARY KEY(${SubscribeColumns[0]})
    )
    """.trimIndent()
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun subscribe(city: Subscribe): Long {
        val db: SQLiteDatabase = writableDatabase
        return try {
            val values = ContentValues().apply {
                put(SubscribeColumns[0], city.cityCode)
                put(SubscribeColumns[1], city.city)
            }
            val ret = db.insert(TABLE_NAME, null, values)
            Log.i("DataBase", "订阅成功")
            ret
        } catch (e: SQLiteException) {
            Log.e("DataBase", "订阅失败", e)
            -1L
        }
    }

    fun unsubscribe(city: Subscribe): Int {
        val db: SQLiteDatabase = writableDatabase
        return try {
            val whereClause = "${SubscribeColumns[0]} = ? AND ${SubscribeColumns[1]} = ?"
            val whereArgs = arrayOf(city.cityCode, city.city)

            val deletedRows = db.delete(TABLE_NAME, whereClause, whereArgs)

            if (deletedRows > 0) {
                Log.i("DataBase", "成功取消订阅：${city.cityCode} (${city.city})")
            } else {
                Log.w("DataBase", "未找到匹配的订阅记录")
            }
            deletedRows
        } catch (e: SQLiteException) {
            Log.e("DataBase", "取消订阅失败：${e.localizedMessage}")
            -1
        }
    }

    @SuppressLint("Range")
    fun getAll(): List<Subscribe> {
        val db: SQLiteDatabase = readableDatabase
        val subscribeList = mutableListOf<Subscribe>()
        var cursor: Cursor? = null
        try {
            cursor = db.query(
                TABLE_NAME,
                SubscribeColumns,
                null, null, null, null, null
            )
            while (cursor.moveToNext()) {
                val cityCode = cursor.getString(cursor.getColumnIndex(SubscribeColumns[0]))
                val city = cursor.getString(cursor.getColumnIndex(SubscribeColumns[1]))
                subscribeList.add(Subscribe(cityCode, city))
            }
            Log.i("DataBase", "成功获取所有订阅记录，共 ${subscribeList.size} 条")
            return subscribeList
        } catch (e: SQLiteException) {
            Log.e("DataBase", "查询订阅列表失败", e)
            return emptyList()
        } finally {
            cursor?.close()
            db.close()
        }
    }
}