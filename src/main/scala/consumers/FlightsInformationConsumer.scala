package consumers

import consumers.deserializer.FlightDeserializer
import kafka.serializer.StringDecoder
import models.FlatFlight
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.{SparkConf, SparkContext}

object FlightsInformationConsumer extends App {

  val conf = new SparkConf()
    .setMaster("local[1]")
    .setAppName("FlightsInformationConsumer")
  val sc = new SparkContext(conf)
  val ssc = new StreamingContext(sc, Seconds(5))

  // make a connection to Kafka and read (key, value) pairs from it

  val kafkaConf = Map(
    "metadata.broker.list" -> sys.env("KAFKA_BROKER"),
    "zookeeper.connect" -> sys.env("ZOOKEEPER_ADDRESS"),
    "group.id" -> "kafka-spark-streaming",
    "zookeeper.connection.timeout.ms" -> "1000")
  val topic = Set("cities")

  val flights = KafkaUtils.createDirectStream[
    String, FlatFlight,
    StringDecoder,
    FlightDeserializer](
    ssc, kafkaConf, topic)

  // TODO map with state calculate total flights / late flights below

  val lateFlights = flights.filter(flight => {
    java.time.Duration.between(flight._2.arrivalActualTimeLocal, flight._2.arrivalExpectedTimeLocal).getSeconds > 30 * 60
  })


  val flightsWithWeather = FlightWeatherMerger.joinFlightsWithWeatherDate(sc, lateFlights)
  flightsWithWeather.count().print()

  LateFlightCalculator.calculateLateFlights(flightsWithWeather)

  val dangerousWeatherStates = List("rain", "fog", "wind", "snow", "sleet", "hail")

  val flightsDelayedDueToWeather = flightsWithWeather.filter(flight => {
    val weatherInfo = flight._2._2
    dangerousWeatherStates.contains(weatherInfo.icon) || weatherInfo.windSpeed > 4 ||
      weatherInfo.visibility < 8 || weatherInfo.precipIntensityMax > 4
  })

  ssc.start()
  ssc.awaitTermination()


}


