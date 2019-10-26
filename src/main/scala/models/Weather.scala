package models

import java.time.LocalDateTime

case class Weather(var airportCode: Option[String], longitude: Double, latitude: Double, timezone: String, daily: DailyListData, alerts: List[Alert])

case class DailyListData(summary: String, icon: String, data: List[SingleDayData])

class Alert(val title: String,
            val time: LocalDateTime,
            val expires: LocalDateTime,
            val description: String,
            val regions: List[String],
            val severity: Option[String],
            val uri: String)

class SingleDayData(val time: LocalDateTime,
                    val summary: String,
                    val icon: String,
                    val sunriseTime: LocalDateTime,
                    val sunsetTime: LocalDateTime,
                    val moonPhase: Double,
                    val precipIntensity: Double,
                    val precipIntensityMax: Double,
                    val precipProbability: Double,
                    val temperatureMin: Double,
                    val temperatureMinTime: LocalDateTime,
                    val temperatureMax: Double,
                    val temperatureMaxTime: LocalDateTime,
                    val apparentTemperatureMin: Long,
                    val apparentTemperatureMinTime: LocalDateTime,
                    val apparentTemperatureMax: Double,
                    val apparentTemperatureMaxTime: LocalDateTime,
                    val dewPoint: Double,
                    val humidity: Double,
                    val windSpeed: Double,
                    val windBearing: Double,
                    val visibility: Double,
                    val cloudCover: Double,
                    val pressure: Double,
                    val ozone: Double)

