package models

import java.io.Serializable
import java.time.LocalDateTime


class Flight(val Departure: DepartureArrival, val Arrival: DepartureArrival, val OperatingCarrier: CarrierInfo) extends Serializable {
}

class DepartureArrival(val AirportCode: String, var ScheduledTimeLocal: ScheduledTime, var ScheduledTimeUTC: ScheduledTime,
                            var ActualTimeLocal: ScheduledTime, var ActualTimeUTC: ScheduledTime, val TimeStatus: Status) extends Serializable

class ScheduledTime(var DateTime: LocalDateTime) extends Serializable

class Status(val Code: String, val Definition: String) extends Serializable

class CarrierInfo(val AirlineID: String, val FlightNumber: Long) extends Serializable
