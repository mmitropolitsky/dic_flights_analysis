

import java.time.LocalDateTime

import connectors.DarkSkyWeatherConnector
import convertors.WeatherToFlatDailyWeatherConverter
import models._
import producers.FlightsInformationProducer
import repositories.cassandra._

object FlightsAnalysisApp extends App {

  val source = "SOF"
  val destination = "FRA"
  var requests = 0
  while (requests < 500) {
    //    val accessToken = LufthansaConnector.getLufthansaAccessToken()
    //    val flights = LufthansaConnector.getStatusForFlightsWithSourceAndDestination(accessToken, source, destination)


    var localTime1 = new ScheduledTime(LocalDateTime.parse("2019-10-28T10:00"))
    var localArrival = new ScheduledTime(LocalDateTime.parse("2019-10-28T10:40"))
    var status = new Status("TEST", "Testing ")
    var dep = new DepartureArrival("FRA", localTime1, localTime1, localArrival, localArrival, status)
    var arr = new DepartureArrival("ARN", localTime1, localTime1, localArrival, localArrival, status)
    var carrierInfo = new CarrierInfo("LH", 1234)
    var flight = new Flight(dep, arr, carrierInfo)
    FlightsInformationProducer.addFlightInformationToKafka(destination, flight)
    requests += 1
    Thread.sleep(1000)
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