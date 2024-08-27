package com.example.weatherapp

import android.view.Window

data class apiCek(
    val main: Main,
    val name: String,
    val weather: List<Weather>,
    val sys: Sys,
    val wind: Wind
)

data class Main(
    val temp: Float,
    val humidity: Int,
    val temp_min: Float,
    val temp_max: Float
)

data class Weather(
    val description: String,
    val icon: String
)

data class Sys(
    val sunrise: Long,
    val sunset: Long
)
data class Wind(
    val speed: Double
)

data class CitySuggestion(
    val name: String,
    val country: String
)

