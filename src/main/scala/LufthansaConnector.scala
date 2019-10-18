package flightsanalysis

import net.liftweb.json._

object LufthansaConnector extends App {

  println("Hello World")

  implicit val formats = DefaultFormats
  case class AccessToken(access_token: String, token_type: String, expires_in: Long)

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
  println(tokenResp.statusCode)
  println(tokenResp.text)
  val accessToken = parse(tokenResp.text).extract[AccessToken].access_token
  println(accessToken)

  val r = requests.get(
            "https://api.lufthansa.com/v1/operations/flightstatus/LH803/2019-10-14",
            headers = Map(
              "Authorization" -> s"Bearer $accessToken",
              "Content-Type" -> "application/json",
              "Accept" -> "application/json"
            )
          )

  println(r.statusCode)
  println(r.data)
  println(r.text)
}
