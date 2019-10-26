package models

import java.time.LocalDateTime

case class FlatDailyWeather(
                             airportDateKey: String,
                             airportCode: String,
                             time: LocalDateTime,
                             summary: String,
                             icon: String,
                             sunriseTime: LocalDateTime,
                             sunsetTime: LocalDateTime,
                             precipIntensityMax: Double,
                             precipProbability: Double,
                             temperatureMin: Double,
                             temperatureMax: Double,
                             apparentTemperatureMin: Double,
                             apparentTemperatureMax: Double,
                             windSpeed: Double,
                             cloudCover: Double,
                             pressure: Double
                           )
