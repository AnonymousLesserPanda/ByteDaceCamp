package com.example.bytedancecamplab3

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.bytedancecamplab3.forecast.SubscribeDataBaseHelper
import com.example.bytedancecamplab3.forecast.SubscribeDataBaseHelper.Subscribe

class AddCityActivity : AppCompatActivity() {
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var provinceSpinner: Spinner
    private lateinit var citySpinner: Spinner
    private lateinit var provinceAdapter: ArrayAdapter<String>
    private lateinit var cityAdapter: ArrayAdapter<String>
    private lateinit var confirmButton: Button
    private var provinceList = emptyList<String>()
    private val subscribeDataBaseHelper = SubscribeDataBaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_city)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        provinceSpinner = findViewById(R.id.provinceSpinner)
        citySpinner = findViewById(R.id.citySpinner)
        confirmButton = findViewById(R.id.confirmButton)

        confirmButton.setOnClickListener {
            confirm()
            finish()
        }

        setSpinners()
        setObservation()

    }

    private fun setSpinners() {
        provinceAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item)
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        provinceSpinner.adapter = provinceAdapter

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
    }

    private fun setObservation() {
        weatherViewModel.cityCodeMap.observe(this) { provinceMap ->
            updateProvinceAdapter(
                provinceMap.keys.toList()
            )
        }

        weatherViewModel.cityList.observe(this) { cities ->
            updateCityAdapter(cities)
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

    private fun confirm() {
        val province = provinceSpinner.selectedItem as String
        val city = citySpinner.selectedItem as String

        if (province.isEmpty() || city.isEmpty()) {
            Toast.makeText(this, "请完整选择省份和城市", Toast.LENGTH_SHORT).show()
            return
        }

        val cityCode = weatherViewModel.cityCodeMap.value?.get(province)?.get(city) ?: return
        subscribeDataBaseHelper.subscribe(Subscribe(cityCode, city))
    }
}