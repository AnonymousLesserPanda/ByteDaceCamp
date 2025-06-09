package com.example.bytedancecamplab3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bytedancecamplab3.forecast.CityForecastFragment
import com.example.bytedancecamplab3.forecast.ForecastAdapter
import com.example.bytedancecamplab3.forecast.SubscribeDataBaseHelper.Subscribe
import com.example.bytedancecamplab3.network.CacheDataBaseHelper.WeatherRecord

class MainActivity : AppCompatActivity() {
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var forecastRecycleView: RecyclerView
    private lateinit var forecastAdapter: ForecastAdapter
    private lateinit var addButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        forecastRecycleView = findViewById(R.id.forecastRecycleView)
        addButton = findViewById(R.id.addButton)

        addButton.setOnClickListener {
            intent = Intent(this, AddCityActivity::class.java)
            startActivity(intent)
        }

        setRecycleView()

        weatherViewModel.subscribeList.observe(this) { list ->
            Log.d("测试", "订阅列表更新")
            weatherViewModel.getWeatherByCityCodes(
                list
            )
        }
        weatherViewModel.forecastList.observe(this) { data ->
            Log.d("测试", "预报数据更新")
            forecastAdapter.submitList(data)
        }

    }

    override fun onResume() {
        super.onResume()
        weatherViewModel.refreshSubscribeList()
    }

    private fun setRecycleView() {
        forecastRecycleView.layoutManager = LinearLayoutManager(this)
        forecastRecycleView.addItemDecoration(
            DividerItemDecoration(
                this, DividerItemDecoration.HORIZONTAL
            )
        )
        forecastAdapter = ForecastAdapter()
        forecastRecycleView.adapter = forecastAdapter

        forecastAdapter.setOnItemClickListener { weatherRecord ->
            showCityForecast(weatherRecord)
        }
    }

    private fun showCityForecast(item: WeatherRecord) {
        Log.d("测试", "唤起fragment")
        val fragment = CityForecastFragment.newInstance(item.cityCode, this.application)
        supportFragmentManager.beginTransaction().add(R.id.cityForecast, fragment, "CITY_FORECAST")
            .addToBackStack(null).commit()
    }
}