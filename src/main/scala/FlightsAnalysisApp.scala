

import java.time.LocalDateTime

import connectors.{DarkSkyWeatherConnector, LufthansaConnector}
import convertors.WeatherToFlatDailyWeatherConverter
import models._
import producers.FlightsInformationProducer
import repositories.cassandra._

import java.time.LocalDate

object FlightsAnalysisApp extends App {

  val source = "FRA"
  val destination = "SOF"
  val destination2 = "ARN"
  val date = LocalDateTime.parse("2019-10-26T10:00").toLocalDate
  var requests = 0
  while (requests < 500) {
//        val accessToken = LufthansaConnector.getLufthansaAccessToken()
//        val flights = LufthansaConnector.getFlightsBySourceDestinationDate(accessToken, source, destination, date)


    var localTime1 = new ScheduledTime(LocalDateTime.parse("2019-10-13T10:00"))
    var localTime1late = new ScheduledTime(LocalDateTime.parse("2019-10-13T15:00"))
    var status = new Status("TEST", "Testing ")
    var dep = new DepartureArrival("FRA", localTime1, localTime1, localTime1, localTime1, status)
    var dep2 = new DepartureArrival("MUN", localTime1, localTime1, localTime1, localTime1, status)
    var arr = new DepartureArrival(destination, localTime1, localTime1, localTime1late, localTime1late, status)
    var arr2 = new DepartureArrival(destination2, localTime1, localTime1, localTime1, localTime1, status)
    var carrierInfo = new CarrierInfo("LH", 1234)
    var flight = new Flight(dep, arr, carrierInfo)
    var flight2 = new Flight(dep2, arr2, carrierInfo)
    FlightsInformationProducer.addFlightInformationToKafka(destination, flight)
    FlightsInformationProducer.addFlightInformationToKafka(destination2, flight2)
    requests += 1
    Thread.sleep(1000)
  }
  val darkSkyWeatherConnector = DarkSkyWeatherConnector

  //  Airport.airportCodeToCoordinatesMap.foreach {
  //    airport =>
  val w = darkSkyWeatherConnector.getWeatherForecastForAirport("SOF")
  //      Thread.sleep(1000)
  //  }

  val cassandra = new WeatherCassandraRepository
  //  cassandra.selectAll()
  val flatDailyWeatherList = WeatherToFlatDailyWeatherConverter.convert(w)
  cassandra.batchSaveFlatWeatherList(flatDailyWeatherList)

}