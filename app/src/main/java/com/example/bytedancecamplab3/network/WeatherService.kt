package com.example.bytedancecamplab3.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("weather/weatherInfo")
    suspend fun getWeather(
        @Query("city") cityCode: String,  // 城市编码
        @Query("key") apiKey: String = "6dc8da14709bbfc6808d1f2735e87c53",     // API 密钥
        @Query("extensions") extensions: String = "all"  // 扩展参数
    ): Response<WeatherResponse>
}

// 主响应数据类
data class WeatherResponse(
    val status: String,
    val count: String,
    val info: String,
    val infocode: String,
    val forecasts: List<Forecast>
)

data class Forecast(
    val city: String,          // 城市名称
    val adcode: String,        // 城市编码
    val province: String,      // 省份名称
    val reporttime: String,    // 预报发布时间
    val casts: List<Cast>      // 预报数据列表
)

data class Cast(
    val date: String,          // 日期（格式：YYYY-MM-DD）
    val week: String,          // 星期几
    val dayweather: String,    // 白天天气现象
    val nightweather: String,  // 夜间天气现象
    val daytemp: String,       // 白天温度（单位：℃）
    val nighttemp: String,     // 夜间温度（单位：℃）
    val daywind: String,       // 白天风向
    val nightwind: String,     // 夜间风向
    val daypower: String,      // 白天风力（单位：级）
    val nightpower: String,    // 夜间风力（单位：级）
    val daytemp_float: String, // 白天温度（小数）
    val nighttemp_float: String// 夜间温度（小数）
)