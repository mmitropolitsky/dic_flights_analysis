package producers

import java.util.Properties

import convertors.{ConverterUtils, FlightToFlatFlightConverter}
import models.{FlatFlight, Flight}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

object FlightsInformationProducer {

  val topic = "cities"
  val brokers = sys.env("KAFKA_BROKER")

  val props = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
  props.put(ProducerConfig.CLIENT_ID_CONFIG, "producers.FlightsInformationProducer")
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "serializers.FlightSerializer")
  val producer = new KafkaProducer[String, FlatFlight](props)

  def addFlightInformationToKafka(source: String, flight: Flight): Unit = {
    //Keys are used to determine the partition within a log to which a message get's appended to
    val data = new ProducerRecord[String, FlatFlight](topic, source, FlightToFlatFlightConverter.convert(flight))
    producer.send(data)
//    producer.close()
    print(data + "\n")
  }
}

