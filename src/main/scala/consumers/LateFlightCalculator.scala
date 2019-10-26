package consumers

import convertors.ConverterUtils
import models.{FlatFlight, Flight}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Durations, State, StateSpec}

object LateFlightCalculator {

  def calculateLateFlights(flights: DStream[(String, FlatFlight)]): Unit = {
    val stateDstream = flights.mapWithState(StateSpec.function(mappingFunc _).timeout(Durations.seconds(10)))
    flights.count().print()
    flights.print()
  }

  def mappingFunc(key: String, value: Option[FlatFlight], state: State[Map[String, List[FlatFlight]]]): Unit = {
    (value, state.getOption()) match {
      // no state yet
      case (newFlight, None) => {
        val map = Map(value.get.airportDateKey -> List(value.get))
        state.update(map)
      }
      // there is state
      case (newFlight, Some(existingState)) => {
        if (existingState.contains(value.get.airportDateKey)) {
          var existingDestinationAndDateRecord = existingState.get(value.get.airportDateKey)
          //          existingDestinationAndDateRecord = value :: existingDestinationAndDateRecord
          //          state.update(updateState)
        } else {
          var updateState = existingState
          updateState += value.get.airportDateKey -> List(value.get)
          state.update(updateState)
        }
      }
    }
  }

}
