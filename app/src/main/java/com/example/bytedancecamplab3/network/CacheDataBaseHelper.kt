package com.example.bytedancecamplab3.network

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class CacheDataBaseHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {
    companion object {
        private const val DATABASE_NAME = "Note.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "weather_record"
        private val weatherRecordColumns = arrayOf("cityCode", "date", "temp", "weather")
    }

    data class WeatherRecord(
        val cityCode: String, val date: String, val temp: String, val weather: String
    )

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
        CREATE TABLE $TABLE_NAME(
        ${weatherRecordColumns[0]} TEXT NOT NULL,
        ${weatherRecordColumns[1]} TEXT NOT NULL,
        ${weatherRecordColumns[2]} TEXT NOT NULL,        
        ${weatherRecordColumns[3]} TEXT NOT NULL,
       PRIMARY KEY(${weatherRecordColumns[0]},${weatherRecordColumns[1]})
    )
    """.trimIndent()
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun addRecord(record: WeatherRecord): Long {
        val db: SQLiteDatabase = writableDatabase
        return try {
            val values = ContentValues().apply {
                put(weatherRecordColumns[0], record.cityCode)
                put(weatherRecordColumns[1], record.date)
                put(weatherRecordColumns[2], record.temp)
                put(weatherRecordColumns[3], record.weather)
            }
            val ret = db.insert(TABLE_NAME, null, values)
            Log.i("DataBase", "添加缓存成功")
            ret
        } catch (e: SQLiteException) {
            Log.e("DataBase", "添加缓存失败", e)
            -1L
        }
    }

    fun delRecord() {}

    fun updateRecord(record: WeatherRecord) {}

    fun findWeatherByCityAndTime(cityCode: String, date: String): List<WeatherRecord> {
        val db: SQLiteDatabase = writableDatabase
        val cursor = db.query(
            TABLE_NAME,
            weatherRecordColumns,
            "${weatherRecordColumns[0]}=? AND ${weatherRecordColumns[1]}=?",
            arrayOf(cityCode, date),
            null,
            null,
            null
        )
        val result = mutableListOf<WeatherRecord>()
        cursor.use { col ->
            while (cursor.moveToNext()) {
                val record = WeatherRecord(
                    col.getString(0), col.getString(1), col.getString(2), col.getString(3)
                )
                result.add(record)
            }
        }
        return result
    }
}