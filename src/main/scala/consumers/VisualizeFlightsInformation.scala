package consumers

import com.datawizards.splot.api.implicits._
import repositories.cassandra.FlightSummaryCassandraRepository

object VisualizeFlightsInformation {

  def visualizeRatioBetweenAllFlightsAndLateFlights(): Unit = {

    val flightSummaryRepo = new FlightSummaryCassandraRepository
    val flightSummaries = flightSummaryRepo.selectAll()

    flightSummaries.foreach { i => {

      Seq(("lateFlights", (i.totalLateFlights.toInt)),
        ("lateFlightsDueToWeather", i.totalLateFlightsDueToWeather.toInt),
        ("flightsOnTime", (i.totalFlights - i.totalLateFlights)))
        .buildPlot().pie()
        .title("Airport code: " + i.airportCode + "; date: " + i.date).display()
    }
    }

    val flightsByAirport = flightSummaries.groupBy(_.airportCode)
    val test = flightsByAirport.mapValues(_.size)
    flightsByAirport.foreach(
      f => {
        var dateFlightStatusAggregated = Map[(String, String), Integer]()
        val flightsByDateAndLateness = f._2.foreach(i => {
          dateFlightStatusAggregated += (i.date, "flightsOnTime") -> (i.totalFlights - i.totalLateFlights)
          dateFlightStatusAggregated += (i.date, "lateFlights") -> i.totalLateFlights
          dateFlightStatusAggregated += (i.date, "lateFlightsDueToWeather") -> i.totalLateFlightsDueToWeather
        })

        dateFlightStatusAggregated.buildPlot().colsBy(_._1._1 + " (" + f._1 + ")")
          .bar(x => x._1._2, x => x._2.toInt).size(1200, 300)
          .display()
      })


  }
}
