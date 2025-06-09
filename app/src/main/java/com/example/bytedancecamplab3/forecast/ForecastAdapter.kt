package com.example.bytedancecamplab3.forecast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bytedancecamplab3.R
import com.example.bytedancecamplab3.network.CacheDataBaseHelper.WeatherRecord

class ForecastAdapter() :
    ListAdapter<WeatherRecord, ForecastAdapter.ForecastViewHolder>(ForecastDiffCallBack()) {
    private var onItemClickListener: ((WeatherRecord) -> Unit)? = null


    inner class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cityText: TextView = itemView.findViewById(R.id.cityText)
        private val tempText: TextView = itemView.findViewById(R.id.tempText)
        private val weatherText: TextView = itemView.findViewById(R.id.weatherText)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    getItem(position)?.let { item ->
                        onItemClickListener?.invoke(item)
                    }
                }
            }
        }

        fun bind(item: WeatherRecord) {
            cityText.text = item.city
            tempText.text = "${item.temp}℃"
            weatherText.text = item.weather
        }
    }

    class ForecastDiffCallBack : ItemCallback<WeatherRecord>() {
        override fun areItemsTheSame(oldItem: WeatherRecord, newItem: WeatherRecord): Boolean {
            return oldItem.cityCode == newItem.cityCode && oldItem.date == newItem.date
        }


        override fun areContentsTheSame(oldItem: WeatherRecord, newItem: WeatherRecord): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.forecast_item, parent, false)
        return ForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setOnItemClickListener(listener: (WeatherRecord) -> Unit) {
        onItemClickListener = listener
    }
}