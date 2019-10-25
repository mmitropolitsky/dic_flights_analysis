import java.time.LocalDateTime

import connectors.{DarkSkyWeatherConnector, LufthansaConnector}
import connectors.LufthansaConnector
import models.{CarrierInfo, DepartureArrival, Flight, ScheduledTime, Status}
import producers.FlightsInformationProducer

object FlightsAnalysisApp extends App {

  val source = "SOF"
  val destination = "FRA"
  var requests = 0
  while (requests < 500) {
//    val accessToken = LufthansaConnector.getLufthansaAccessToken()
//    val flights = LufthansaConnector.getStatusForFlightsWithSourceAndDestination(accessToken, source, destination)


    var localTime1 = new ScheduledTime(LocalDateTime.parse("2019-10-13T10:00"))
    var status = new Status("TEST", "Testing ")
    var dep = new DepartureArrival("FRA", localTime1, localTime1, localTime1, localTime1, status)
    var arr = new DepartureArrival("SOF", localTime1, localTime1, localTime1, localTime1, status)
    var carrierInfo = new CarrierInfo("LH", 1234)
    var flight = new Flight(dep, arr, carrierInfo)
    FlightsInformationProducer.addFlightInformationToKafka(destination, flight)
    requests += 1
    Thread.sleep(1000)

//    val darkSkyWeatherConnector = DarkSkyWeatherConnector
//    darkSkyWeatherConnector.getWeatherForecastForAirport("SOF")

  }

}