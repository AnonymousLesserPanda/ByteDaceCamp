package com.example.bytedancecamplab3.network

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.util.Log
import com.example.bytedancecamplab3.network.CacheDataBaseHelper.WeatherRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class WeatherServiceWithCache(private val context: Context) {
    private val weatherService = RetrofitClient.instance
    private val cacheDataBaseHelper = CacheDataBaseHelper(context)

    suspend fun getWeather(cityCode: String): List<WeatherRecord> {
        val cache = cacheDataBaseHelper.findWeatherByCityAndTime(cityCode, getCurrentDate())
        if (cache.size == 4) {
            return cache
        } else {
            var ret = emptyList<WeatherRecord>()
            try {
                val response = withContext(Dispatchers.IO) {
                    weatherService.getWeather(cityCode)
                }
                ret = processResponse(response.body())
                return ret
            } catch (e: Exception) {
                Log.e("WeatherServiceWithCache", "获取天气数据错误", e)
                return ret
            }
        }
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        return sdf.format(calendar.time)
    }

    private fun processResponse(response: WeatherResponse?): List<WeatherRecord> {
        if (response == null) {
            return emptyList()
        }
        val forecast = response.forecasts[0]
        val cityCode = forecast.adcode
        val city = forecast.city
        val casts = forecast.casts
        val records: MutableList<WeatherRecord> = mutableListOf()
        casts.forEach { cast ->
            val record = WeatherRecord(cityCode, city, cast.date, cast.daytemp, cast.dayweather)
            if (cacheDataBaseHelper.findWeatherByCityAndTime(cityCode, cast.date).isEmpty()) {
                cacheDataBaseHelper.addRecord(record)
            } else {
                cacheDataBaseHelper.updateRecord(record)
            }
            records.add(record)
        }
        return records
    }
}