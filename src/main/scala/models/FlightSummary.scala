package models

case class FlightSummary(
                          var airportCode: String,
                          var date: String,
                          var totalFlights: Integer,
                          var totalLateFlights: Integer,
//                          lateFlights: List[FlatFlight],
                          var totalLateFlightsDueToWeather: Integer)
//                          totalLateFlightsDueToRain: Integer,
//                          totalLateFlightsDueToVisibility: Integer,
//                          totalLateFlightsDueToWind: Integer)
