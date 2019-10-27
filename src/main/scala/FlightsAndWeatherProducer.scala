import connectors.{DarkSkyWeatherConnector, LufthansaConnector}
import convertors.WeatherToFlatDailyWeatherConverter
import models._
import producers.FlightsInformationProducer
import repositories.cassandra.WeatherCassandraRepository


object FlightsAndWeatherProducer extends App {

  val darkSkyWeatherConnector = DarkSkyWeatherConnector
  val cassandra = new WeatherCassandraRepository

  Airport.airportCodeToCoordinatesMap.foreach {
    airport =>
      val forecast = darkSkyWeatherConnector.getWeatherForecastForAirport(airport._1)
      val forecastList = WeatherToFlatDailyWeatherConverter.convert(forecast)
      cassandra.batchSaveFlatWeatherList(forecastList)
      Thread.sleep(1000)

      val history = darkSkyWeatherConnector.getPastWeatherForAirport(airport._1, 5)
      history.foreach(h => {
        val historyList = WeatherToFlatDailyWeatherConverter.convert(h)
        cassandra.batchSaveFlatWeatherList(historyList)
      })
  }



  val source = "FRA"
  val dates = List("2019-10-23", "2019-10-24", "2019-10-25", "2019-10-26", "2019-10-27")
  var requests = 0
//  while (requests < 500) {

    Airport.airportCodeToCoordinatesMap.foreach(a => {
      val accessToken = LufthansaConnector.getLufthansaAccessToken()
      dates.foreach(d => {
        val flights = LufthansaConnector.getStatusForFlightsWithSourceAndDestination(accessToken.toString, source, a._1, d)
        flights.foreach(f => FlightsInformationProducer.addFlightInformationToKafka(a._1, f))
        requests += 1
        Thread.sleep(2000)
      })
    })


//  }

}