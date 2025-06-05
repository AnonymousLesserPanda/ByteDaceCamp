package com.example.bytedancecamplab2

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NoteDataBaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "Note.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "info_card"
        private val infoCardColumns = arrayOf("id", "userId", "title", "brief", "time")
    }

    data class InfoCard(
        val id: Long,
        val userId: Long,
        val title: String,
        val brief: String,
        val time: String
    )

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME(
                ${infoCardColumns[0]} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${infoCardColumns[1]} INTEGER ,
                ${infoCardColumns[2]} TEXT NOT NULL,
                ${infoCardColumns[3]} TEXT NOT NULL,
                ${infoCardColumns[4]} TEXT NOT NULL                
            )
        """.trimIndent()
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun addInfoCard(userId: Long, title: String, brief: String): Long {
        val db: SQLiteDatabase = writableDatabase
        return try {
            val values = ContentValues().apply {
                put(infoCardColumns[1], userId)
                put(infoCardColumns[2], title)
                put(infoCardColumns[3], brief)
                put(infoCardColumns[4], getFormattedTimestamp())
            }
            db.insert(TABLE_NAME, null, values)
        } catch (e: SQLiteException) {
            Log.e("DataBase", "新建笔记失败", e)
            -1L
        } finally {
            db.close()
        }
    }

    fun updateInfoCard(id: Long, title: String, brief: String): Long {
        val db: SQLiteDatabase = writableDatabase
        return try {
            val values = ContentValues().apply {
                put(infoCardColumns[2], title)
                put(infoCardColumns[3], brief)
                put(infoCardColumns[4], getFormattedTimestamp())
            }
            db.update(
                TABLE_NAME,
                values,
                "id = ?",
                arrayOf(id.toString())
            ).toLong()
        } catch (e: SQLiteException) {
            Log.e("DataBase", "更新笔记失败", e)
            -1L
        } finally {
            db.close()
        }
    }

    fun findInfoCardByUserId(userId: Long): List<InfoCard> {
        val db: SQLiteDatabase = readableDatabase
        val cursor: Cursor = db.query(
            /* table = */ TABLE_NAME,
            /* columns = */ infoCardColumns,
            /* selection = */ "${infoCardColumns[1]} = ?",
            /* selectionArgs = */ arrayOf(userId.toString()),
            /* groupBy = */ null, /* having = */ null, /* orderBy = */ null
        )
        val res = mutableListOf<InfoCard>()
        try {
            val idIndex = cursor.getColumnIndex(infoCardColumns[0])
            val titleIndex = cursor.getColumnIndex(infoCardColumns[2])
            val briefIndex = cursor.getColumnIndex(infoCardColumns[3])
            val timeIndex = cursor.getColumnIndex(infoCardColumns[4])

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idIndex)
                val title = cursor.getString(titleIndex)
                val brief = cursor.getString(briefIndex)
                val time = cursor.getString(timeIndex)
                res.add(InfoCard(id, userId, title, brief, time))
            }
        } catch (e: Exception) {
            Log.e("DataBase", "获取用户备忘录预览信息错误", e)
        } finally {
            cursor.close()
        }
        return res
    }

    private fun getFormattedTimestamp(): String {
        val timestampMillis = System.currentTimeMillis()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestampMillis))
    }
}
