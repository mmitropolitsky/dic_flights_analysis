package connectors

import models.{CarrierInfo, DepartureArrival, Flight}
import net.liftweb.json._
import serializers.LocalDateTimeFromStringSerializer

object LufthansaConnector {


  implicit val formats = DefaultFormats + new LocalDateTimeFromStringSerializer

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


  def getStatusForFlightsWithSourceAndDestination(accessToken: String, source: String, destination: String, date: String): List[Flight] = {
    val flightStatus = requests.get(
      s"https://api.lufthansa.com/v1/operations/flightstatus/route/$source/$destination/$date",
      headers = Map(
        "Authorization" -> s"Bearer $accessToken",
        "Content-Type" -> "application/json",
        "Accept" -> "application/json"
      )
    )
    convertJsonToFlightStatusResource(flightStatus.text)
  }

  def convertJsonToFlightStatusResource(flightsJson: String): List[Flight] = {
    val flights = parse(flightsJson)
    val flightJsonObject = flights.asInstanceOf[JObject].obj.head.value.asInstanceOf[JObject].obj.head
    try {
      val flightJsonObjectArray = flightJsonObject.value.asInstanceOf[JObject].obj.head.value.asInstanceOf[_root_.net.liftweb.json.JsonAST.JArray].arr
      val arr = flightJsonObjectArray.map(_.asInstanceOf[JObject].obj)

      arr.map { i =>
        new Flight(i.head.value.extract[DepartureArrival], i(1).value.extract[DepartureArrival], i(3).value.extract[CarrierInfo])
      }
    } catch {
      case e: MappingException => List[Flight]()

    }

  }
}
