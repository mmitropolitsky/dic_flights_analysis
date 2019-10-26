package models

import java.time.LocalDateTime

case class FlatFlight(airportDateKey: String,
                      departure: String,
                      arrival: String,
                      carrier: String,
                      departureExpectedTimeUTC: LocalDateTime,
                      departureActualTimeUTC: LocalDateTime,
                      departureExpectedTimeLocal: LocalDateTime,
                      departureActualTimeLocal: LocalDateTime,
                      arrivalExpectedTimeUTC: LocalDateTime,
                      arrivalActualTimeUTC: LocalDateTime,
                      arrivalExpectedTimeLocal: LocalDateTime,
                      arrivalActualTimeLocal: LocalDateTime)