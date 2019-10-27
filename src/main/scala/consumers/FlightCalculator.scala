package consumers

import com.datastax.spark.connector.SomeColumns
import com.datastax.spark.connector.streaming._
import models.{FlatDailyWeather, FlatFlight, FlightSummary}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Durations, State, StateSpec}

object FlightCalculator {


  def calculateLateFlights(flights: DStream[(String, (FlatFlight, FlatDailyWeather))]): Unit = {
    val stateDstream = flights.mapWithState(StateSpec.function(mappingFunc _).timeout(Durations.seconds(10)))
    flights.count().print()
    flights.print()
  }

  def calculateTotalFlights(flights: DStream[(String, FlatFlight)]): Unit = {
    val dstream = flights.mapWithState(StateSpec.function(mappingFuncTotalCount _).timeout(Durations.seconds(60)))
    dstream.saveToCassandra("weather", "flight_summary",
      SomeColumns("airportCode", "date", "totalFlights", "totalLateFlights", "totalLateFlightsDueToWeather"))
  }

  def mappingFuncTotalCount(key: String, value: Option[FlatFlight], state: State[Map[String, FlightSummary]]): FlightSummary = {
    val airportCode = key.split(",")(0)
    val date = key.split(",")(1)
    (key, state.getOption()) match {

      case (k, None) =>
        val fs = FlightSummary(airportCode = airportCode, date = date, totalFlights = 1, totalLateFlights = 0, totalLateFlightsDueToWeather = 0)
        val map = Map(k -> fs)
        state.update(map)
        fs

      case (k, Some(existingState)) =>
        val existingSummary = existingState.get(k)
        existingSummary.get.totalFlights += 1
        val map = Map(k -> existingSummary.get)
        state.update(map)
        existingSummary.get
    }
  }

  def mappingFunc(key: String, value: Option[(FlatFlight, FlatDailyWeather)], state: State[Map[String, FlightSummary]]): Unit = {
    (key, state.getOption()) match {
      // no state yet
      case (k, None) => {
        val airportCode = key.split(",")(0)
        val date = key.split(",")(1)
        val fs = FlightSummary(airportCode = airportCode, date = date, totalFlights = 1, totalLateFlights = 0, totalLateFlightsDueToWeather = 0)
        val map = Map(k -> fs)
        // TODO convert to flight summary the map
        state.update(map)
      }
      // there is state
      case (k, Some(existingState)) => {
        if (existingState.contains(value.get._1.airportDateKey)) {
          var existingDestinationAndDateRecord = existingState.get(value.get._1.airportDateKey)
          //          existingDestinationAndDateRecord = value :: existingDestinationAndDateRecord
          //          state.update(updateState)
        } else {
          var updateState = existingState
          //          updateState += value.get._1.airportDateKey -> List(value.get)
          state.update(updateState)
        }
      }
    }
  }

}
