package convertors

import com.datastax.driver.core.Row
import models.FlightSummary

object CassandraRowToFlightSummaryConverter {
  def convert(row: Row): FlightSummary = {
    FlightSummary(row.getString("airportCode"),
      row.getString("date"),
      row.getInt("totalFlights"),
      row.getInt("totalLateFlights"),
      row.getInt("totalLateFlightsDueToWeather")
    )
  }
}
