package ru.gennady.kurbesov.simplesmartweather.model

class WeatherResponse {
    var lat: Double = 0.0
    var lon: Double = 0.0
    var timezone: String = ""
    var current: WeatherInfo? = null
}