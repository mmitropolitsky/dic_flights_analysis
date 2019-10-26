package consumers

import consumers.deserializer.FlightDeserializer
import kafka.serializer.StringDecoder
import models.FlatFlight
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka.KafkaUtils

object FlightsInformationConsumer extends App {

  val THIRTY_MINUTES_IN_SECONDS = 30 * 60

  //  val conf = new SparkConf()
  //    .setMaster("local[1]")
  //    .setAppName()
  val sparkSession = SparkSession.builder()
    .master("local[1]")
    .appName("FlightsInformationConsumer")
    .config("spark.cassandra.connection.host", "localhost")
    .getOrCreate()

  val ssc = new StreamingContext(sparkSession.sparkContext, Seconds(5))

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

  val lateFlights = flights.filter(flight => {
    java.time.Duration.between(flight._2.arrivalActualTimeLocal, flight._2.arrivalExpectedTimeLocal).getSeconds > 5 * 60 //THIRTY_MINUTES_IN_SECONDS
  })

  val l = sparkSession
    .read
    .format("org.apache.spark.sql.cassandra")
    .options(Map("table" -> "alert", "keyspace" -> "weather"))
    .load

  l.explain

  //  val mergedWithWeather = FlightWeatherMerger.joinFlightsWithWeatherDate(sc, flights)

  LateFlightCalculator.calculateLateFlights(flights)


  ssc.start()
  ssc.awaitTermination()
}


