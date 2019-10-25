package models

import java.time.LocalDateTime


case class Flight(Departure: DepartureArrival, Arrival: DepartureArrival, OperatingCarrier: CarrierInfo)

case class DepartureArrival(AirportCode: String, var ScheduledTimeLocal: ScheduledTime, var ScheduledTimeUTC: ScheduledTime,
                            var ActualTimeLocal: ScheduledTime, var ActualTimeUTC: ScheduledTime, TimeStatus: Status)

class ScheduledTime(var DateTime: LocalDateTime)

case class Status(Code: String, Definition: String)

case class CarrierInfo(AirlineID: String, FlightNumber: Long)
