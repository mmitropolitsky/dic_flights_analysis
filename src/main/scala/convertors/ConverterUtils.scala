package convertors

import java.time.{LocalDateTime, ZoneId}
import java.time.format.DateTimeFormatter

object ConverterUtils {

  /**
    *
    * @param airportCode
    * @param date
    * @return code of type SOF,2019-10-26
    */
  def convertAirportCodeAndDateToPair(airportCode: String, date: LocalDateTime): String = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val dateString = date.format(formatter)
    s"$airportCode,$dateString"
  }

  def convertLocalDateTimeToTimestamp(ldt: LocalDateTime): Long = {
    ldt.atZone(ZoneId.systemDefault()).toInstant.toEpochMilli
  }
}
