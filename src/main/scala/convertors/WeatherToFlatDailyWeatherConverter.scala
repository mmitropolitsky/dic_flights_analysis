package convertors

import models.{FlatDailyWeather, Weather}

object WeatherToFlatDailyWeatherConverter {

  def convert(weather: Weather): List[FlatDailyWeather] = {
    var list = List[FlatDailyWeather]()

    for (daily <- weather.daily.data) {
      val flatDailyWeather = FlatDailyWeather(ConverterUtils.convertAirportCodeAndDateToPair(weather.airportCode.get, daily.time),
        weather.airportCode.get, daily.time, daily.summary, daily.icon, daily.sunriseTime, daily.sunsetTime, daily.precipIntensityMax,
        daily.precipProbability, daily.temperatureMin, daily.temperatureMax, daily.apparentTemperatureMin, daily.apparentTemperatureMax,
        daily.windSpeed, daily.cloudCover, daily.pressure)

      list = flatDailyWeather :: list
    }

    list
  }

}
