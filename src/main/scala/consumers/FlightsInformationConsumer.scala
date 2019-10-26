package consumers

import consumers.deserializer.FlightDeserializer
import convertors.ConverterUtils
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

  def mappingFunc(key: String, value: Option[Flight], state: State[Map[String, List[Flight]]]): Unit = {
    val destinationAndDate = ConverterUtils.convertAirportCodeAndDateToPair(key, value.get.Arrival.ActualTimeUTC.DateTime)
    (value, state.getOption()) match {
      // no state yet
      case (newFlight, None) => {
        val map = Map(destinationAndDate -> List(value.get))
        state.update(map)
      }
      // there is state
      case (newFlight, Some(existingState)) => {
        if (existingState.contains(destinationAndDate)) {
          var existingDestinationAndDateRecord = existingState.get(destinationAndDate)
          //          existingDestinationAndDateRecord = value :: existingDestinationAndDateRecord
          //          state.update(updateState)
        } else {
          var updateState = existingState
          updateState += destinationAndDate -> List(value.get)
          state.update(updateState)
        }
      }
      }
  }

  val stateDstream = flights.mapWithState(StateSpec.function(mappingFunc _).timeout(Durations.seconds(10)))
  flights.count().print()
  flights.print()
//  val lateFlights = flights.filter {
//    i => i._2.Arrival.ActualTimeUTC.DateTime.compareTo(i._2.Arrival.ScheduledTimeUTC.DateTime) > 0
//  }
//  lateFlights.print().timeout(Durations.seconds(10))
  ssc.start()
  ssc.awaitTermination()
}


