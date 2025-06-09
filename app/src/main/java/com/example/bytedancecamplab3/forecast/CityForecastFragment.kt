package com.example.bytedancecamplab3.forecast

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bytedancecamplab3.R
import com.example.bytedancecamplab3.network.CacheDataBaseHelper.WeatherRecord
import com.example.bytedancecamplab3.network.WeatherServiceWithCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CityForecastFragment(application: Application) : Fragment() {
    private lateinit var cityForecastRecycleView: RecyclerView
    private lateinit var cityForecastAdapter: CityForeCastAdapter
    private val weatherService = WeatherServiceWithCache(application)

    companion object {
        fun newInstance(cityCode: String,application: Application): CityForecastFragment {
            Log.d("测试","新建fragment")
            val fragment = CityForecastFragment(application)
            val args = Bundle()
            args.putString("cityCode", cityCode)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_city_forecast, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            cityForecastRecycleView = view.findViewById(R.id.forecastRecycleView)
            setRecycleView()
            viewLifecycleOwner.lifecycleScope.launch {
                val cityCode = it.getString("cityCode")
                val weather = withContext(Dispatchers.IO) {
                    if (cityCode != null) {
                        weatherService.getWeather(cityCode)
                    }else{
                        emptyList()
                    }
                }
                cityForecastAdapter.submitList(weather)
            }
        }
    }

    private fun setRecycleView() {
        cityForecastRecycleView.layoutManager = LinearLayoutManager(this.context)
        cityForecastAdapter = CityForeCastAdapter()
        cityForecastRecycleView.adapter = cityForecastAdapter
    }

}