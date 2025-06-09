package com.example.bytedancecamplab3

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bytedancecamplab3.forecast.SubscribeDataBaseHelper
import com.example.bytedancecamplab3.forecast.SubscribeDataBaseHelper.Subscribe
import com.example.bytedancecamplab3.network.CacheDataBaseHelper.WeatherRecord
import com.example.bytedancecamplab3.network.WeatherServiceWithCache
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val _forecastList = MutableLiveData<List<WeatherRecord>>()
    val forecastList: LiveData<List<WeatherRecord>> = _forecastList
    private val _subscribeList = MutableLiveData<List<Subscribe>>()
    val subscribeList: LiveData<List<Subscribe>> = _subscribeList
    private val weatherService = WeatherServiceWithCache(application)
    private val assetManager = application.assets
    private val ADCODE_FILE = "adcode.json"
    private val _cityCodeMap = MutableLiveData<Map<String, Map<String, String>>>()
    val cityCodeMap: LiveData<Map<String, Map<String, String>>> get() = _cityCodeMap
    private val _cityList = MutableLiveData<List<String>>()
    val cityList: LiveData<List<String>> get() = _cityList
    private val subscribeDataBaseHelper = SubscribeDataBaseHelper(application)

    init {
        viewModelScope.launch {
            try {
                _cityCodeMap.postValue(parseCityData())
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "城市数据加载失败", e)
                _cityCodeMap.postValue(emptyMap())
            }
        }
    }

    fun refreshSubscribeList(){
        _subscribeList.postValue(subscribeDataBaseHelper.getAll())
    }

    fun updateCityOptions(province: String) {
        val cities = _cityCodeMap.value?.get(province)?.keys?.toList() ?: emptyList()
        _cityList.postValue(cities)
    }

    fun getWeatherByCityCodes(subscribes: List<Subscribe>) {
        viewModelScope.launch {
            try {
                var weathers = mutableListOf<WeatherRecord>()
                for (subscribe in subscribes) {
                    val weather = withContext(Dispatchers.IO) {
                        weatherService.getWeather(subscribe.cityCode)
                    }
                    weathers.add(weather[0])
                }
                _forecastList.postValue(weathers)
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "获取天气数据错误", e)
            }
        }
    }

    private fun readJsonFromAssets(): String? {
        return try {
            val inputStream: InputStream = assetManager.open(ADCODE_FILE)
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charset.forName("UTF-8"))
        } catch (e: IOException) {
            Log.e("WeatherViewModel", "adcode.json读取失败", e)
            null
        } catch (e: Exception) {
            Log.e("WeatherViewModel", "adcode.json读取失败", e)
            null
        }
    }

    /**
     * @return 省-市-cityCode三级Map
     */
    private suspend fun parseCityData(): Map<String, Map<String, String>> {
        return withContext(Dispatchers.IO) {
            try {
                val json = readJsonFromAssets()
                val gson = Gson()
                val provinces = gson.fromJson(json, Array<Province>::class.java).toList()
                val ret = provinces.associate { province ->
                    province.name to province.city.associate { city ->
                        city.name to city.adcode
                    }
                }
                ret
            } catch (e: IOException) {
                Log.e("WeatherViewModel", "读取文件失败", e)
                emptyMap<String, Map<String, String>>()
                throw RuntimeException("读取文件失败: ${e.message}")
            } catch (e: JsonSyntaxException) {
                Log.e("WeatherViewModel", "json解析失败", e)
                emptyMap<String, Map<String, String>>()
                throw RuntimeException("JSON解析失败: ${e.message}")
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "json文件未知错误", e)
                Log.d("WeatherViewModel", "json文件未知错误")
                emptyMap<String, Map<String, String>>()
                throw RuntimeException("json文件未知错误: ${e.message}")
            }
        }
    }
}

// 城市列表类
data class Province(
    val name: String, val city: List<City>
)

data class City(
    val adcode: String, val name: String
)