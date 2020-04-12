package ru.gennady.kurbesov.simplesmartweather.weather

import ru.gennady.kurbesov.simplesmartweather.model.CityInfo

interface IWeatherClientListener {
    fun onError()
    fun onCity(city: String)
    fun onWeatherReceive(data: WeatherData)
    fun onWeatherHint(hint: String)
    fun onCityList(citys: List<CityInfo>)
}