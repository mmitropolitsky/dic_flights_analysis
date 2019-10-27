package models

case class FlightSummary(
                          airportCode: String,
                          totalFlights: Integer,
                          totalLateFlights: Integer,
//                          lateFlights: List[FlatFlight],
                          totalLateFlightsDueToWeather: Integer)
//                          totalLateFlightsDueToRain: Integer,
//                          totalLateFlightsDueToVisibility: Integer,
//                          totalLateFlightsDueToWind: Integer)
