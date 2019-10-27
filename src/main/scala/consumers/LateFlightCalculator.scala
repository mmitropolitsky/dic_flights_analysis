package consumers

import models.{FlatDailyWeather, FlatFlight, FlightSummary}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Durations, State, StateSpec}

object LateFlightCalculator {

  def calculateLateFlights(flights: DStream[(String, (FlatFlight, FlatDailyWeather))]): Unit = {
    val stateDstream = flights.mapWithState(StateSpec.function(mappingFunc _).timeout(Durations.seconds(10)))
    flights.count().print()
    flights.print()
  }

  def mappingFunc(key: String, value: Option[(FlatFlight, FlatDailyWeather)], state: State[Map[String, FlightSummary]]): Unit = {
    (value, state.getOption()) match {
      // no state yet
      case (newFlight, None) => {
        val map = Map(value.get._1.airportDateKey -> List(value.get))
        // TODO convert to flight summary the map
//        state.update(map)
      }
      // there is state
      case (newFlight, Some(existingState)) => {
        if (existingState.contains(value.get._1.airportDateKey)) {
          var existingDestinationAndDateRecord = existingState.get(value.get._1.airportDateKey)
          //          existingDestinationAndDateRecord = value :: existingDestinationAndDateRecord
          //          state.update(updateState)
        } else {
          var updateState = existingState
          updateState += value.get._1.airportDateKey -> List(value.get)
          state.update(updateState)
        }
      }
    }
  }

}
