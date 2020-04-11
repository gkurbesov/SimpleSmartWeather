package ru.gennady.kurbesov.simplesmartweather.model

class WeatherInfo {
    var dt: Long? = null
    var sunrise: Long? = null
    var sunset: Long? = null
    var temp: Float = 0f
    var feels_like: Float = 0f
    var pressure: Int = 0
    var humidity: Int = 0
    var dew_point: Float = 0f
    var uvi: Float? = null
    var clouds: Int = 0
    var visibility: Int? = null
    var wind_speed: Float = 0f
    var wind_deg: Int? = null
    var weather: ArrayList<WeatherState>? = null
}