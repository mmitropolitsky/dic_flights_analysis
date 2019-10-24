import java.time.LocalDateTime

import net.liftweb.json._
import org.codehaus.jackson.map.ObjectMapper

object LufthansaConnector extends App {

  class LocalDateTimeSerializer extends Serializer[LocalDateTime] {
    private val LocalDateTimeClass = classOf[LocalDateTime]

    def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), LocalDateTime] = {
      case (TypeInfo(LocalDateTimeClass, _), json) => json match {

        case JString(dTF) => if (dTF.takeRight(1) == "Z") LocalDateTime.parse(dTF.dropRight(1))
        else LocalDateTime.parse(dTF);
        case x => throw new MappingException("Can't convert " + x + " to LocalDateTime")
      }
    }

    def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
      case x: LocalDateTime => JString(x.toString)
    }
  }

  implicit val formats = DefaultFormats + new LocalDateTimeSerializer

  case class AccessToken(access_token: String, token_type: String, expires_in: Long)

  case class DepartureArrival(AirportCode: String, var ScheduledTimeLocal: ScheduledTime, var ScheduledTimeUTC: ScheduledTime,
                              var ActualTimeLocal: ScheduledTime, var ActualTimeUTC: ScheduledTime, TimeStatus: Status)

  class ScheduledTime(var DateTime: LocalDateTime) {
    println(DateTime)
  }

  case class Status(Code: String, Definition: String)

  case class CarrierInfo(AirlineID: String, FlightNumber: Long)

  case class Flight(Departure: DepartureArrival, Arrival: DepartureArrival, OperatingCarrier: CarrierInfo)

  case class Flights(Flight: Array[DepartureArrival])

  case class FlightStatusResource(Flights: Flights)

  val tokenResp = requests.post(
    "https://api.lufthansa.com/v1/oauth/token",
    headers = Map(
      "Content-Type" -> "application/x-www-form-urlencoded"
    ),
    data = Map(
      "client_id" -> sys.env("LUFTHANSA_CLIENT_ID"),
      "client_secret" -> sys.env("LUFTHANSA_CLIENT_SECRET"),
      "grant_type" -> "client_credentials"
    )
  )

  val accessToken = parse(tokenResp.text).extract[AccessToken].access_token

  val flightStatus = requests.get(
    "https://api.lufthansa.com/v1/operations/flightstatus/route/SOF/FRA/2019-10-18",
    headers = Map(
      "Authorization" -> s"Bearer $accessToken",
      "Content-Type" -> "application/json",
      "Accept" -> "application/json"
    )
  )

  convertJsonToFlightStatusResource(flightStatus.text)

  def convertJsonToFlightStatusResource(flightsJson: String): String = {

    val flights = parse(flightsJson)
    val flightJsonObject = flights.asInstanceOf[JObject].obj.head.value.asInstanceOf[JObject].obj.head
    val flightJsonObjectArray = flightJsonObject.value.asInstanceOf[JObject].obj.head.value.asInstanceOf[_root_.net.liftweb.json.JsonAST.JArray].arr
    var dep = flightJsonObjectArray.head.asInstanceOf[JObject].obj.head.value.extract[DepartureArrival]
    ""
  }
}
