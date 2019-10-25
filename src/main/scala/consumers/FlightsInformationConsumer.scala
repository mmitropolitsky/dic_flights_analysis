package consumers

import consumers.deserializer.FlightDeserializer
import kafka.serializer.StringDecoder
import models.Flight
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
    String, Flight,
    StringDecoder,
    FlightDeserializer](
    ssc, kafkaConf, topic)


  val count = flights.count()
  count.print()
  ssc.start()
  ssc.awaitTermination()
}


