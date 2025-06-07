package com.example.bytedancecamplab3

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.AdapterView
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.bytedancecamplab3.network.WeatherResponse

class MainActivity : AppCompatActivity() {
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var tempText: TextView
    private lateinit var provinceSpinner: Spinner
    private lateinit var citySpinner: Spinner
    private lateinit var provinceAdapter: ArrayAdapter<String>
    private lateinit var cityAdapter: ArrayAdapter<String>
    private var provinceList = emptyList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("测试", "onCreate调用")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        bind()
        setSpinners()

        weatherViewModel.cityCodeMap.observe(this) { provinceMap ->
            updateProvinceAdapter(
                provinceMap.keys.toList()
            )
        }

        weatherViewModel.cityList.observe(this) { cities ->
            updateCityAdapter(cities)
        }

        weatherViewModel.weather.observe(this) { data -> updateWeather(data) }
    }

    private fun updateWeather(data: WeatherResponse) {
        Log.d("测试", "UI更新数据:${data}")
        tempText.text = "温度：${data.forecasts[0].casts[0].daytemp}"
    }

    private fun bind() {
        tempText = findViewById(R.id.tempText)
        provinceSpinner = findViewById(R.id.provinceSpinner)
        citySpinner = findViewById(R.id.citySpinner)
    }

    private fun setSpinners() {
        // 初始化省份适配器
        provinceAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item)
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        provinceSpinner.adapter = provinceAdapter

        // 初始化城市适配器
        cityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item)
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        citySpinner.adapter = cityAdapter

        provinceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val selectedProvince = provinceList.getOrNull(position)
                selectedProvince?.let {
                    weatherViewModel.updateCityOptions(it)
                } ?: run {
                    cityAdapter.clear()
                    cityAdapter.notifyDataSetChanged()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        citySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val selectedProvince = provinceSpinner.selectedItem as? String
                val selectedCity = parent?.getItemAtPosition(position) as? String

                if (!selectedProvince.isNullOrBlank() && !selectedCity.isNullOrBlank()) {
                    weatherViewModel.getWeatherByCityCode(selectedProvince, selectedCity)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun updateProvinceAdapter(provinces: List<String>) {
        provinceList = provinces
        provinceAdapter.clear()
        provinceAdapter.addAll(provinces)
        provinceAdapter.notifyDataSetChanged()
    }

    private fun updateCityAdapter(cities: List<String>) {
        cityAdapter.clear()
        cityAdapter.addAll(cities)
        cityAdapter.notifyDataSetChanged()
    }
}