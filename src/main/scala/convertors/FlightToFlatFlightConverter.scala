package convertors

import models.{FlatFlight, Flight}

object FlightToFlatFlightConverter {

  def convert(flight: Flight): FlatFlight = {

    FlatFlight(ConverterUtils.convertAirportCodeAndDateToPair(flight.Arrival.AirportCode, flight.Arrival.ActualTimeLocal.DateTime),
      flight.Departure.AirportCode, flight.Arrival.AirportCode, flight.OperatingCarrier.AirlineID + flight.OperatingCarrier.FlightNumber,
      flight.Departure.ScheduledTimeUTC.DateTime, flight.Departure.ActualTimeUTC.DateTime, flight.Departure.ScheduledTimeLocal.DateTime, flight.Departure.ActualTimeLocal.DateTime,
      flight.Arrival.ScheduledTimeUTC.DateTime, flight.Arrival.ActualTimeUTC.DateTime, flight.Arrival.ScheduledTimeLocal.DateTime, flight.Arrival.ActualTimeLocal.DateTime)


  }
}
