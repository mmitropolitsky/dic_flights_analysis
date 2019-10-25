package connectors

import java.time.LocalDateTime

import models.{CarrierInfo, DepartureArrival, Flight}
import net.liftweb.json._

object LufthansaConnector {

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

  def getLufthansaAccessToken(): String = {
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

    parse(tokenResp.text).extract[AccessToken].access_token
  }


  def getStatusForFlightsWithSourceAndDestination(accessToken: String, source: String, destination: String): Array[Flight] = {
    val flightStatus = requests.get(
      s"https://api.lufthansa.com/v1/operations/flightstatus/route/$source/$destination/2019-10-18",
      headers = Map(
        "Authorization" -> s"Bearer $accessToken",
        "Content-Type" -> "application/json",
        "Accept" -> "application/json"
      )
    )
    convertJsonToFlightStatusResource(flightStatus.text)
  }

  def convertJsonToFlightStatusResource(flightsJson: String): Array[Flight] = {
    val flights = parse(flightsJson)
    val flightJsonObject = flights.asInstanceOf[JObject].obj.head.value.asInstanceOf[JObject].obj.head
    val flightJsonObjectArray = flightJsonObject.value.asInstanceOf[JObject].obj.head.value.asInstanceOf[_root_.net.liftweb.json.JsonAST.JArray].arr
    val arr = flightJsonObjectArray.map(_.asInstanceOf[JObject].obj)
    val arraysOfDeparturesJsonObjectArray = arr.map(_.head.value.extract[DepartureArrival])
    val arraysOfArrivalsJsonObjectArray = arr.map(_ (1).value.extract[DepartureArrival])
    val arraysOfOperatingCarrierJsonObjectArray = arr.map(_ (3).value.extract[CarrierInfo])
    Array.fill[Flight](1)(Flight(arraysOfDeparturesJsonObjectArray.head, arraysOfArrivalsJsonObjectArray.head, arraysOfOperatingCarrierJsonObjectArray.head))
  }
}
