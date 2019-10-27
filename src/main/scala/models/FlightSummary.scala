package models

case class FlightSummary(totalFlights: Integer,
                         totalLateFlights: Integer,
                         lateFlights: List[FlatFlight],
                         totalLateFlightsDueToRain: Integer,
                         totalLateFlightsDueToVisibility: Integer,
                         totalLateFlightsDueToWind: Integer)
