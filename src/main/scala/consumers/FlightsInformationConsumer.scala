package consumers

import com.datastax.driver.core.Cluster
import kafka.serializer.StringDecoder
import models.FlatFlight
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.{SparkConf, SparkContext}

object FlightsInformationConsumer extends App {


  // SPARK CONF
  val conf = new SparkConf()
    .setMaster("local[1]")
    .setAppName("FlightsInformationConsumer")
  val sc = new SparkContext(conf)
  val ssc = new StreamingContext(sc, Seconds(5))
  ssc.checkpoint("checkpoints")

  // CASSANDRA CONF
  val cluster = Cluster.builder().withoutJMXReporting()
    .addContactPoint("127.0.0.1").build()

  val session = cluster.connect()

  // KAFKA CONF
  val kafkaConf = Map(
    "metadata.broker.list" -> sys.env("KAFKA_BROKER"),
    "zookeeper.connect" -> sys.env("ZOOKEEPER_ADDRESS"),
    "group.id" -> "kafka-spark-streaming",
    "zookeeper.connection.timeout.ms" -> "100000")
  val topic = Set("cities")

  val dangerousWeatherStates = List("rain", "fog", "wind", "snow", "sleet", "hail")


  val flights = KafkaUtils.createDirectStream[
    String, FlatFlight,
    StringDecoder,
    FlightDeserializer](
    ssc, kafkaConf, topic)

  // Update total count into state and in Cassandra
  FlightCalculator.calculateTotalFlights(flights)
  flights.count().print()

  // Filter late flights
  val lateFlights = flights.filter(flight => {
    Math.abs(java.time.Duration.between(flight._2.arrivalActualTimeLocal, flight._2.arrivalExpectedTimeLocal).getSeconds) > 30 * 60
  })


  // Count late flights and update them in the state and Cassandra
  FlightCalculator.calculateLateFlights(lateFlights)

  // Join every delayed flight with weather data for the same destination airport and date.
  val flightsWithWeather = FlightWeatherMerger.joinFlightsWithWeatherDate(sc, lateFlights)
  flightsWithWeather.print()

  // Filter the joined DStream with some [arbitrary] predicate function to say which delays are caused by weather conditions
  val flightsDelayedDueToWeather = flightsWithWeather.filter(flight => {
    val weatherInfo = flight._2._2
    dangerousWeatherStates.contains(weatherInfo.icon) || weatherInfo.windSpeed > 4 ||
      weatherInfo.visibility < 8 || weatherInfo.precipIntensityMax > 4
  })

  // Count delayed flights based on weather function
  FlightCalculator.calculateLateFlightsDueToWeather(flightsDelayedDueToWeather)

  ssc.start()
  ssc.awaitTermination()

  session.close()
}




