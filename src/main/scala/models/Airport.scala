package models

object Airport {

  // It is enough to put {latitude},{longitude} pair. It will be added to the URL
  val airportCodeToCoordinatesMap: Map[String, String] = Map(
    "LHR" -> "51.4700256,-0.4564895",
    "SOF" -> "42.6934317,23.4049293",
    "ARN" -> "59.3262416,17.8416281"
  )

}
