package consumers

import convertors.ConverterUtils
import models.Flight
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Durations, State, StateSpec}

object LateFlightCalculator {

  def calculateLateFlights(flights: DStream[(String, Flight)]): Unit = {
    val stateDstream = flights.mapWithState(StateSpec.function(mappingFunc _).timeout(Durations.seconds(10)))
    flights.count().print()
    flights.print()
  }

  def mappingFunc(key: String, value: Option[Flight], state: State[Map[String, List[Flight]]]): Unit = {
    val destinationAndDate = ConverterUtils.convertAirportCodeAndDateToPair(key, value.get.Arrival.ActualTimeUTC.DateTime)
    (value, state.getOption()) match {
      // no state yet
      case (newFlight, None) => {
        val map = Map(destinationAndDate -> List(value.get))
        state.update(map)
      }
      // there is state
      case (newFlight, Some(existingState)) => {
        if (existingState.contains(destinationAndDate)) {
          var existingDestinationAndDateRecord = existingState.get(destinationAndDate)
          //          existingDestinationAndDateRecord = value :: existingDestinationAndDateRecord
          //          state.update(updateState)
        } else {
          var updateState = existingState
          updateState += destinationAndDate -> List(value.get)
          state.update(updateState)
        }
      }
    }
  }

}
