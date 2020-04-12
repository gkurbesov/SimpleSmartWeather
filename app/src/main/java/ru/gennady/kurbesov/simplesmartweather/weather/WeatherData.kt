package ru.gennady.kurbesov.simplesmartweather.weather

data class WeatherData(
    val temp: Float,
    val temp_like: Float,
    val pressure: Int,
    val humidity: Int,
    val cloud: Int,
    val wind: Float,
    val rain: Boolean,
    val weather_desc: String
)