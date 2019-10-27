package consumers

import com.datawizards.splot.api.implicits._
import repositories.cassandra.FlightSummaryCassandraRepository
import vegas._
import vegas.render.WindowRenderer._

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

  }

}
