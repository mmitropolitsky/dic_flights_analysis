import connectors.LufthansaConnector
import models._
import producers.FlightsInformationProducer

object FlightsAndWeatherProducer extends App {

  val source = "FRA"
  val dates = List("2019-10-23", "2019-10-24", "2019-10-25", "2019-10-26", "2019-10-27")
  var requests = 0
  while (requests < 500) {

    Airport.airportCodeToCoordinatesMap.foreach(a => {
      val accessToken = LufthansaConnector.getLufthansaAccessToken()
      dates.foreach(d => {
        val flights = LufthansaConnector.getStatusForFlightsWithSourceAndDestination(accessToken, source, a._1, d)
        flights.foreach(f => FlightsInformationProducer.addFlightInformationToKafka(a._1, f))
        requests += 1
        Thread.sleep(2000)
      })
    })


  }
  //  val darkSkyWeatherConnector = DarkSkyWeatherConnector

  //  Airport.airportCodeToCoordinatesMap.foreach {
  //    airport =>
  //  val w = darkSkyWeatherConnector.getWeatherForecastForAirport("ARN")
  //      Thread.sleep(1000)
  //  }

  //  val cassandra = new WeatherCassandraRepository
  //  cassandra.selectAll()
  //  val flatDailyWeatherList = WeatherToFlatDailyWeatherConverter.convert(w)
  //  cassandra.batchSaveFlatWeatherList(flatDailyWeatherList)

}