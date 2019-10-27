package visualizer

import com.datawizards.splot.api.implicits._
import com.datawizards.splot.model.ImageFormats
import repositories.cassandra.FlightSummaryCassandraRepository

object VisualizeFlightsInformation extends App {

  val flightSummaryRepo = new FlightSummaryCassandraRepository

  while (true) {
    val flightSummaries = flightSummaryRepo.selectAll()

    flightSummaries.foreach { i => {

      Seq(("lateFlights", i.totalLateFlights.toInt),
        ("lateFlightsDueToWeather", i.totalLateFlightsDueToWeather.toInt),
        ("flightsOnTime", i.totalFlights - i.totalLateFlights))
        .buildPlot().pie()
        .title("Airport code: " + i.airportCode + "; date: " + i.date)
        .save(s"./screenshots/pie_image${System.currentTimeMillis()}.png", ImageFormats.PNG)
    }
    }

    val flightsByAirport = flightSummaries.groupBy(_.airportCode)
    val test = flightsByAirport.mapValues(_.size)


    // BAR CHART LEGEND:
    // Total Flights = TF
    // Flights On Time = FoT
    // Total Late Flights = TLF
    // Late Flights Due To Weather = LFW


    flightsByAirport.foreach(
      f => {
        var dateFlightStatusAggregated = Map[(String, String), Integer]()
        f._2.foreach(i => {
          dateFlightStatusAggregated += (i.date, "TF") -> i.totalFlights
          dateFlightStatusAggregated += (i.date, "FoT") -> (i.totalFlights - i.totalLateFlights)
          dateFlightStatusAggregated += (i.date, "TLF") -> i.totalLateFlights
          dateFlightStatusAggregated += (i.date, "LFW") -> i.totalLateFlightsDueToWeather
        })

        dateFlightStatusAggregated.buildPlot().colsBy(_._1._1 + " (" + f._1 + ")")
          .bar(x => x._1._2, x => x._2.toInt).size(1600, 300)
          .save(s"./screenshots/bar_image${System.currentTimeMillis()}.png", ImageFormats.PNG)
      })
    Thread.sleep(15000)
  }

}
