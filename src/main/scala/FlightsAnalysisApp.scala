import connectors.{DarkSkyWeatherConnector, LufthansaConnector}
import producers.FlightsInformationProducer

object FlightsAnalysisApp extends App {

//  val source = "SOF"
//  val destination = "FRA"
//  val accessToken = LufthansaConnector.getLufthansaAccessToken()
//  val flights = LufthansaConnector.getStatusForFlightsWithSourceAndDestination(accessToken, source, destination)
//  FlightsInformationProducer.addFlightInformationToKafka(destination, flights.head)
  val darkSkyWeatherConnector = DarkSkyWeatherConnector
  darkSkyWeatherConnector.getWeatherForecastForAirport("SOF")

}