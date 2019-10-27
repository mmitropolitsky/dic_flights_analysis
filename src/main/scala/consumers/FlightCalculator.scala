package consumers

import com.datastax.spark.connector.SomeColumns
import com.datastax.spark.connector.streaming._
import models.{FlatDailyWeather, FlatFlight, FlightSummary}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Durations, State, StateSpec}

object FlightCalculator {


  def calculateTotalFlights(flights: DStream[(String, FlatFlight)]): Unit = {
    val dstream = flights.mapWithState(StateSpec.function(mappingFuncTotalCount _).timeout(Durations.seconds(60)))
    dstream.saveToCassandra("weather", "flight_summary",
      SomeColumns("airportCode", "date", "totalFlights"))
  }

  def calculateLateFlights(flights: DStream[(String, FlatFlight)]): Unit = {
    val stateDstream = flights.mapWithState(StateSpec.function(mappingFuncLateFlightTotalCount _).timeout(Durations.seconds(10)))
    stateDstream.saveToCassandra("weather", "flight_summary",
      SomeColumns("airportCode", "date", "totalLateFlights"))
  }

  def calculateLateFlightsDueToWeather(flights: DStream[(String, (FlatFlight, FlatDailyWeather))]): Unit = {
    val stateDstream = flights.mapWithState(StateSpec.function(mappingFuncLateFlightDueToWeatherTotalCount _).timeout(Durations.seconds(10)))
    stateDstream.saveToCassandra("weather", "flight_summary",
      SomeColumns("airportCode", "date", "totalLateFlightsDueToWeather"))
  }

  def mappingFuncTotalCount(key: String,
                            value: Option[FlatFlight],
                            state: State[Map[String, FlightSummary]]): FlightSummary = {
    val airportCode = key.split(",")(0)
    val date = key.split(",")(1)
    (key, state.getOption()) match {

      case (k, None) =>
        val fs = FlightSummary(airportCode = airportCode, date = date, totalFlights = 1, totalLateFlights = 0, totalLateFlightsDueToWeather = 0)
        val map = Map(k -> fs)
        if (!state.isTimingOut()) {
          state.update(map)
        }
        fs

      case (k, Some(existingState)) =>
        val existingSummary = existingState.get(k)
        existingSummary.get.totalFlights += 1
        val map = Map(k -> existingSummary.get)
        if (!state.isTimingOut()) {
          state.update(map)
        }
        existingSummary.get
    }
  }

  def mappingFuncLateFlightTotalCount(key: String,
                                      value: Option[FlatFlight],
                                      state: State[Map[String, FlightSummary]]): FlightSummary = {
    val airportCode = key.split(",")(0)
    val date = key.split(",")(1)
    (key, state.getOption()) match {

      case (k, None) =>
        val fs = FlightSummary(airportCode = airportCode, date = date, totalFlights = 1, totalLateFlights = 1, totalLateFlightsDueToWeather = 0)
        val map = Map(k -> fs)
        if (!state.isTimingOut()) {
          state.update(map)
        }
        fs

      case (k, Some(existingState)) =>
        val existingSummary = existingState.get(k)
        existingSummary.get.totalLateFlights += 1
        val map = Map(k -> existingSummary.get)
        if (!state.isTimingOut()) {
          state.update(map)
        }
        existingSummary.get
    }
  }

  def mappingFuncLateFlightDueToWeatherTotalCount(key: String,
                                                  value: Option[(FlatFlight, FlatDailyWeather)],
                                                  state: State[Map[String, FlightSummary]]): FlightSummary = {
    val airportCode = key.split(",")(0)
    val date = key.split(",")(1)
    (key, state.getOption()) match {

      case (k, None) =>
        val fs = FlightSummary(airportCode = airportCode, date = date, totalFlights = 1, totalLateFlights = 1, totalLateFlightsDueToWeather = 1)
        val map = Map(k -> fs)
        if (!state.isTimingOut()) {
          state.update(map)
        }
        fs

      case (k, Some(existingState)) =>
        val existingSummary = existingState.get(k)
        existingSummary.get.totalLateFlightsDueToWeather += 1
        val map = Map(k -> existingSummary.get)
        if (!state.isTimingOut()) {
          state.update(map)
        }

        existingSummary.get
    }
  }

}
