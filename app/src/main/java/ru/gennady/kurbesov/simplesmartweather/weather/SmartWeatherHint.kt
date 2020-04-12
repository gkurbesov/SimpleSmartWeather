package ru.gennady.kurbesov.simplesmartweather.weather

class SmartWeatherHint {
    companion object {
        fun check(temp: Float, wind: Float, cloud: Int, isRain: Boolean): String {
            var hint = when (temp.toInt()) {
                in -80..0 -> {
                    "На улице очень холодно, одевайтесь теплее! ❄"
                }
                in 0..16 -> {
                    "Рекомендуется надеть теплые вещи" +
                            if (temp in 10..16 && wind > 3.5) " и ветровку" else ""
                }
                in 20..50  -> {
                    if(!isRain){
                        when {
                            cloud < 10 -> "Возьмите с собой головной убор \uD83E\uDDE2"
                            temp >= 28 -> "Пейте больше жидкости \uD83E\uDD64"
                            else -> ""
                        }
                    } else ""
                }
                else -> ""
            }
            if (temp > 0 && isRain) hint += ". Возьмите с собой зонтик ☂"
            return if (hint.isNotEmpty())
                hint.trim('.', ' ')
            else
                "Пока у нас нет рекомендаций. Погода кажется оптимальной ☺️"
        }
    }
}