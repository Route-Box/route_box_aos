package com.daval.routebox.presentation.ui.route.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.daval.routebox.databinding.ItemWeatherBinding
import com.daval.routebox.domain.model.WeatherData

class WeatherRVAdapter(
    private var weatherList: ArrayList<WeatherData>
): RecyclerView.Adapter<WeatherRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WeatherRVAdapter.ViewHolder {
        val binding: ItemWeatherBinding = ItemWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherRVAdapter.ViewHolder, position: Int) {
        holder.bind(weatherList[position])
    }

    override fun getItemCount(): Int = weatherList.size

    inner class ViewHolder(val binding: ItemWeatherBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(weather: WeatherData) {
            Log.d("ROUTE-TEST", "weather = $weather")
            binding.weather = weather
        }
    }

    fun addAllItems(weatherItems: ArrayList<WeatherData>) {
        weatherList.addAll(weatherItems)
        this.notifyDataSetChanged()
    }

    fun resetAllItems(weatherItems: ArrayList<WeatherData>) {
        weatherList = weatherItems
        this.notifyDataSetChanged()
    }
}