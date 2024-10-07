package com.daval.routebox.presentation.ui.route.write.convenience

import com.daval.routebox.R

enum class WeatherType(val weatherName: String, val weatherImg: Int) {
    PTY1("비", R.drawable.ic_weather_pty_1),
    PTY2("비/눈", R.drawable.ic_weather_pty_2),
    PTY3("눈", R.drawable.ic_weather_pty_3),
    PTY4("소나기", R.drawable.ic_weather_pty_4),
    PTY5("빗방울", R.drawable.ic_weather_pty_5),
    PTY6("빗방울눈날림", R.drawable.ic_weather_pty_6),
    PTY7("눈날림", R.drawable.ic_weather_pty_7),
    SKY1("맑음", R.drawable.ic_weather_sky_1),
    SKY3("구름많음", R.drawable.ic_weather_sky_3),
    SKY4("흐림", R.drawable.ic_weather_sky_4)
}

fun returnWeatherType(category: String, value: Int): WeatherType {
    return if (category == "PTY") {
        when (value) {
            1 -> WeatherType.PTY1
            2 -> WeatherType.PTY2
            3 -> WeatherType.PTY3
            4 -> WeatherType.PTY4
            5 -> WeatherType.PTY5
            6 -> WeatherType.PTY6
            else -> WeatherType.PTY7
        }
    } else {
        when (value) {
            1 -> WeatherType.SKY1
            3 -> WeatherType.SKY3
            else -> WeatherType.SKY4
        }
    }
}