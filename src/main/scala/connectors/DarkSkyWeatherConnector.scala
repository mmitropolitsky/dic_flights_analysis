package connectors

import java.time.{LocalDateTime, ZoneId, ZoneOffset}

import models.{Airport, Weather}
import net.liftweb.json._
import serializers.LocalDateTimeFromTimestampSerializer

object DarkSkyWeatherConnector {

  implicit private val formats: Formats = DefaultFormats + new LocalDateTimeFromTimestampSerializer

  private val secret = sys.env("DARK_SKY_WEATHER_API_SECRET")

  private var apiCalls: Int = 0

  def getPastWeatherForAirport(airportCode: String = "ARN", daysBefore: Int = 1): List[Weather] = {
    var weatherList = List[Weather]()
    if (apiCalls < 900) {
      val airportCoordinates = Airport.airportCodeToCoordinatesMap.get(airportCode)
      for (i <- 1 to daysBefore) {
        val unixTimestamp = LocalDateTime.now().minusDays(i).atZone(ZoneId.systemDefault()).toEpochSecond

        val airportCoordinates = Airport.airportCodeToCoordinatesMap.get(airportCode)
        val weatherResponse = requests.get(
          s"https://api.darksky.net/forecast/$secret/${airportCoordinates.get},$unixTimestamp?units=si&exclude=minutely,currently,hourly",
          headers = Map()
        )

        apiCalls = weatherResponse.headers("x-forecast-api-calls").head.toInt

        val weather = parse(weatherResponse.text).extract[Weather]
        weather.airportCode = Option(airportCode)

        weatherList = weather :: weatherList
        Thread.sleep(1000)
      }
    } else {
      throw new Exception("Daily limit reached for Dark Sky API.")
    }

    weatherList
  }

  def getWeatherForecastForAirport(airportCode: String = "ARN"): Weather = {

    if (apiCalls < 900) {
      val airportCoordinates = Airport.airportCodeToCoordinatesMap.get(airportCode)
      val weatherResponse = requests.get(
        s"https://api.darksky.net/forecast/$secret/${airportCoordinates.get}?units=si&exclude=minutely,currently,hourly",
        headers = Map()
      )

      apiCalls = weatherResponse.headers("x-forecast-api-calls").head.toInt

      val testString =
        """{
                "latitude": 59.3262416,
                "longitude": 17.8416281,
                "timezone": "Europe/Stockholm",
                "daily": {
                  "summary": "Light rain tomorrow and Sunday, with high temperatures falling to 4°C next Friday.",
                  "icon": "rain",
                  "data": [
                    {
                      "time": 1571954400,
                      "summary": "Mostly cloudy throughout the day.",
                      "icon": "partly-cloudy-day",
                      "sunriseTime": 1571982674,
                      "sunsetTime": 1572016592,
                      "moonPhase": 0.91,
                      "precipIntensity": 0.0047,
                      "precipIntensityMax": 0.0205,
                      "precipIntensityMaxTime": 1571972400,
                      "precipProbability": 0.18,
                      "precipType": "rain",
                      "temperatureHigh": 14.01,
                      "temperatureHighTime": 1571997600,
                      "temperatureLow": 5.38,
                      "temperatureLowTime": 1572066000,
                      "apparentTemperatureHigh": 13.64,
                      "apparentTemperatureHighTime": 1571997600,
                      "apparentTemperatureLow": 2.66,
                      "apparentTemperatureLowTime": 1572069600,
                      "dewPoint": 8.25,
                      "humidity": 0.82,
                      "pressure": 1004.7,
                      "windSpeed": 6.16,
                      "windGust": 15.2,
                      "windGustTime": 1571958000,
                      "windBearing": 212,
                      "cloudCover": 0.59,
                      "uvIndex": 1,
                      "uvIndexTime": 1572001200,
                      "visibility": 10.756,
                      "ozone": 242.5,
                      "temperatureMin": 8.23,
                      "temperatureMinTime": 1572040800,
                      "temperatureMax": 14.01,
                      "temperatureMaxTime": 1571997600,
                      "apparentTemperatureMin": 5.38,
                      "apparentTemperatureMinTime": 1572040800,
                      "apparentTemperatureMax": 13.64,
                      "apparentTemperatureMaxTime": 1571997600
                    },
                    {
                      "time": 1572040800,
                      "summary": "Light rain in the morning and afternoon.",
                      "icon": "rain",
                      "sunriseTime": 1572069222,
                      "sunsetTime": 1572102832,
                      "moonPhase": 0.95,
                      "precipIntensity": 0.3947,
                      "precipIntensityMax": 2.4569,
                      "precipIntensityMaxTime": 1572069600,
                      "precipProbability": 0.95,
                      "precipType": "rain",
                      "temperatureHigh": 12.3,
                      "temperatureHighTime": 1572084000,
                      "temperatureLow": 4.15,
                      "temperatureLowTime": 1572156000,
                      "apparentTemperatureHigh": 11.93,
                      "apparentTemperatureHighTime": 1572084000,
                      "apparentTemperatureLow": 0.83,
                      "apparentTemperatureLowTime": 1572159600,
                      "dewPoint": 5.65,
                      "humidity": 0.85,
                      "pressure": 1002.04,
                      "windSpeed": 4.88,
                      "windGust": 13.2,
                      "windGustTime": 1572080400,
                      "windBearing": 239,
                      "cloudCover": 0.92,
                      "uvIndex": 1,
                      "uvIndexTime": 1572087600,
                      "visibility": 15.071,
                      "ozone": 232.7,
                      "temperatureMin": 5,
                      "temperatureMinTime": 1572127200,
                      "temperatureMax": 12.3,
                      "temperatureMaxTime": 1572084000,
                      "apparentTemperatureMin": 2.66,
                      "apparentTemperatureMinTime": 1572069600,
                      "apparentTemperatureMax": 11.93,
                      "apparentTemperatureMaxTime": 1572084000
                    },
                    {
                      "time": 1572127200,
                      "summary": "Light rain in the morning and afternoon.",
                      "icon": "rain",
                      "sunriseTime": 1572155770,
                      "sunsetTime": 1572189073,
                      "moonPhase": 0.99,
                      "precipIntensity": 0.2247,
                      "precipIntensityMax": 1.0211,
                      "precipIntensityMaxTime": 1572159600,
                      "precipProbability": 0.84,
                      "precipType": "rain",
                      "temperatureHigh": 6.06,
                      "temperatureHighTime": 1572177600,
                      "temperatureLow": 0.19,
                      "temperatureLowTime": 1572238800,
                      "apparentTemperatureHigh": 2.67,
                      "apparentTemperatureHighTime": 1572192000,
                      "apparentTemperatureLow": -2.67,
                      "apparentTemperatureLowTime": 1572238800,
                      "dewPoint": 2.44,
                      "humidity": 0.87,
                      "pressure": 1003.39,
                      "windSpeed": 3.25,
                      "windGust": 10.35,
                      "windGustTime": 1572177600,
                      "windBearing": 289,
                      "cloudCover": 0.89,
                      "uvIndex": 1,
                      "uvIndexTime": 1572174000,
                      "visibility": 12.452,
                      "ozone": 269.2,
                      "temperatureMin": 1.56,
                      "temperatureMinTime": 1572217200,
                      "temperatureMax": 6.06,
                      "temperatureMaxTime": 1572177600,
                      "apparentTemperatureMin": 0.49,
                      "apparentTemperatureMinTime": 1572217200,
                      "apparentTemperatureMax": 3.18,
                      "apparentTemperatureMaxTime": 1572138000
                    },
                    {
                      "time": 1572217200,
                      "summary": "Mostly cloudy throughout the day.",
                      "icon": "partly-cloudy-day",
                      "sunriseTime": 1572242325,
                      "sunsetTime": 1572275309,
                      "moonPhase": 0.03,
                      "precipIntensity": 0.0068,
                      "precipIntensityMax": 0.0324,
                      "precipIntensityMaxTime": 1572274800,
                      "precipProbability": 0.17,
                      "precipType": "rain",
                      "temperatureHigh": 5.73,
                      "temperatureHighTime": 1572264000,
                      "temperatureLow": -0.01,
                      "temperatureLowTime": 1572321600,
                      "apparentTemperatureHigh": 2.04,
                      "apparentTemperatureHighTime": 1572264000,
                      "apparentTemperatureLow": -4.11,
                      "apparentTemperatureLowTime": 1572318000,
                      "dewPoint": -0.11,
                      "humidity": 0.84,
                      "pressure": 1013.73,
                      "windSpeed": 3.66,
                      "windGust": 10.27,
                      "windGustTime": 1572289200,
                      "windBearing": 341,
                      "cloudCover": 0.59,
                      "uvIndex": 1,
                      "uvIndexTime": 1572260400,
                      "visibility": 15.497,
                      "ozone": 290.6,
                      "temperatureMin": 0.19,
                      "temperatureMinTime": 1572238800,
                      "temperatureMax": 5.73,
                      "temperatureMaxTime": 1572264000,
                      "apparentTemperatureMin": -3.3,
                      "apparentTemperatureMinTime": 1572303600,
                      "apparentTemperatureMax": 2.04,
                      "apparentTemperatureMaxTime": 1572264000
                    },
                    {
                      "time": 1572303600,
                      "summary": "Partly cloudy throughout the day.",
                      "icon": "partly-cloudy-day",
                      "sunriseTime": 1572328873,
                      "sunsetTime": 1572361553,
                      "moonPhase": 0.06,
                      "precipIntensity": 0.0014,
                      "precipIntensityMax": 0.0072,
                      "precipIntensityMaxTime": 1572318000,
                      "precipProbability": 0.06,
                      "precipType": "rain",
                      "temperatureHigh": 4.75,
                      "temperatureHighTime": 1572350400,
                      "temperatureLow": 0.68,
                      "temperatureLowTime": 1572411600,
                      "apparentTemperatureHigh": 1,
                      "apparentTemperatureHighTime": 1572350400,
                      "apparentTemperatureLow": -2.77,
                      "apparentTemperatureLowTime": 1572404400,
                      "dewPoint": -1.86,
                      "humidity": 0.74,
                      "pressure": 1022.31,
                      "windSpeed": 4.12,
                      "windGust": 10.63,
                      "windGustTime": 1572318000,
                      "windBearing": 322,
                      "cloudCover": 0.31,
                      "uvIndex": 1,
                      "uvIndexTime": 1572346800,
                      "visibility": 16.093,
                      "ozone": 278.8,
                      "temperatureMin": -0.01,
                      "temperatureMinTime": 1572321600,
                      "temperatureMax": 4.75,
                      "temperatureMaxTime": 1572350400,
                      "apparentTemperatureMin": -4.11,
                      "apparentTemperatureMinTime": 1572318000,
                      "apparentTemperatureMax": 1,
                      "apparentTemperatureMaxTime": 1572350400
                    },
                    {
                      "time": 1572390000,
                      "summary": "Mostly cloudy throughout the day.",
                      "icon": "cloudy",
                      "sunriseTime": 1572415422,
                      "sunsetTime": 1572447798,
                      "moonPhase": 0.1,
                      "precipIntensity": 0.004,
                      "precipIntensityMax": 0.0116,
                      "precipIntensityMaxTime": 1572408000,
                      "precipProbability": 0.1,
                      "precipType": "rain",
                      "temperatureHigh": 5.02,
                      "temperatureHighTime": 1572436800,
                      "temperatureLow": 1.73,
                      "temperatureLowTime": 1572469200,
                      "apparentTemperatureHigh": 1.82,
                      "apparentTemperatureHighTime": 1572436800,
                      "apparentTemperatureLow": -0.93,
                      "apparentTemperatureLowTime": 1572469200,
                      "dewPoint": -1.85,
                      "humidity": 0.74,
                      "pressure": 1025.84,
                      "windSpeed": 3.28,
                      "windGust": 9.59,
                      "windGustTime": 1572411600,
                      "windBearing": 293,
                      "cloudCover": 0.63,
                      "uvIndex": 1,
                      "uvIndexTime": 1572433200,
                      "visibility": 16.093,
                      "ozone": 283.8,
                      "temperatureMin": 0.68,
                      "temperatureMinTime": 1572411600,
                      "temperatureMax": 5.02,
                      "temperatureMaxTime": 1572436800,
                      "apparentTemperatureMin": -2.77,
                      "apparentTemperatureMinTime": 1572404400,
                      "apparentTemperatureMax": 1.82,
                      "apparentTemperatureMaxTime": 1572436800
                    },
                    {
                      "time": 1572476400,
                      "summary": "Mostly cloudy throughout the day.",
                      "icon": "partly-cloudy-day",
                      "sunriseTime": 1572501972,
                      "sunsetTime": 1572534044,
                      "moonPhase": 0.13,
                      "precipIntensity": 0.0177,
                      "precipIntensityMax": 0.0952,
                      "precipIntensityMaxTime": 1572501600,
                      "precipProbability": 0.34,
                      "precipType": "rain",
                      "temperatureHigh": 6.06,
                      "temperatureHighTime": 1572523200,
                      "temperatureLow": 0.82,
                      "temperatureLowTime": 1572580800,
                      "apparentTemperatureHigh": 3.34,
                      "apparentTemperatureHighTime": 1572523200,
                      "apparentTemperatureLow": -2.72,
                      "apparentTemperatureLowTime": 1572577200,
                      "dewPoint": -0.07,
                      "humidity": 0.79,
                      "pressure": 1018.63,
                      "windSpeed": 3.39,
                      "windGust": 8.2,
                      "windGustTime": 1572537600,
                      "windBearing": 306,
                      "cloudCover": 0.73,
                      "uvIndex": 1,
                      "uvIndexTime": 1572519600,
                      "visibility": 16.093,
                      "ozone": 291.8,
                      "temperatureMin": 1.36,
                      "temperatureMinTime": 1572562800,
                      "temperatureMax": 6.06,
                      "temperatureMaxTime": 1572523200,
                      "apparentTemperatureMin": -2.55,
                      "apparentTemperatureMinTime": 1572562800,
                      "apparentTemperatureMax": 3.34,
                      "apparentTemperatureMaxTime": 1572523200
                    },
                    {
                      "time": 1572562800,
                      "summary": "Overcast throughout the day.",
                      "icon": "cloudy",
                      "sunriseTime": 1572588521,
                      "sunsetTime": 1572620292,
                      "moonPhase": 0.17,
                      "precipIntensity": 0.007,
                      "precipIntensityMax": 0.0351,
                      "precipIntensityMaxTime": 1572577200,
                      "precipProbability": 0.17,
                      "precipType": "rain",
                      "temperatureHigh": 4.48,
                      "temperatureHighTime": 1572609600,
                      "temperatureLow": -0.85,
                      "temperatureLowTime": 1572649200,
                      "apparentTemperatureHigh": 1,
                      "apparentTemperatureHighTime": 1572609600,
                      "apparentTemperatureLow": -3.8,
                      "apparentTemperatureLowTime": 1572660000,
                      "dewPoint": -2.2,
                      "humidity": 0.76,
                      "pressure": 1020.92,
                      "windSpeed": 3.5,
                      "windGust": 6.37,
                      "windGustTime": 1572631200,
                      "windBearing": 4,
                      "cloudCover": 0.98,
                      "uvIndex": 1,
                      "uvIndexTime": 1572606000,
                      "visibility": 16.093,
                      "ozone": 290.1,
                      "temperatureMin": -0.85,
                      "temperatureMinTime": 1572649200,
                      "temperatureMax": 4.48,
                      "temperatureMaxTime": 1572609600,
                      "apparentTemperatureMin": -3.78,
                      "apparentTemperatureMinTime": 1572649200,
                      "apparentTemperatureMax": 1,
                      "apparentTemperatureMaxTime": 1572609600
                    }
                  ]
                },
                "alerts": [
                  {
                    "title": "Flood Watch for Mason, WA",
                    "time": 1509993360,
                    "expires": 1510036680,
                    "description": "...FLOOD WATCH REMAINS IN EFFECT THROUGH LATE MONDAY NIGHT...\nTHE FLOOD WATCH CONTINUES FOR\n* A PORTION OF NORTHWEST WASHINGTON...INCLUDING THE FOLLOWING\nCOUNTY...MASON.\n* THROUGH LATE FRIDAY NIGHT\n* A STRONG WARM FRONT WILL BRING HEAVY RAIN TO THE OLYMPICS\nTONIGHT THROUGH THURSDAY NIGHT. THE HEAVY RAIN WILL PUSH THE\nSKOKOMISH RIVER ABOVE FLOOD STAGE TODAY...AND MAJOR FLOODING IS\nPOSSIBLE.\n* A FLOOD WARNING IS IN EFFECT FOR THE SKOKOMISH RIVER. THE FLOOD\nWATCH REMAINS IN EFFECT FOR MASON COUNTY FOR THE POSSIBILITY OF\nAREAL FLOODING ASSOCIATED WITH A MAJOR FLOOD.\n",
                    "uri": "http://alerts.weather.gov/cap/wwacapget.php?x=WA1255E4DB8494.FloodWatch.1255E4DCE35CWA.SEWFFASEW.38e78ec64613478bb70fc6ed9c87f6e6"
                  }
                ],
                "flags": {
                  "sources": [
                    "meteoalarm",
                    "cmc",
                    "gfs",
                    "icon",
                    "isd",
                    "madis"
                  ],
                  "meteoalarm-license": "Based on data from EUMETNET - MeteoAlarm [https://www.meteoalarm.eu/]. Time delays between this website and the MeteoAlarm website are possible; for the most up to date information about alert levels as published by the participating National Meteorological Services please use the MeteoAlarm website.",
                  "nearest-station": 6.495,
                  "units": "si"
                },
                "offset": 2
              }"""

      val weather = parse(weatherResponse.text).extract[Weather]
      weather.airportCode = Option(airportCode)

      weather
    } else {
      throw new Exception("Daily limit reached for Dark Sky API.")
    }

  }
}
