package producers

import java.util
import java.util.Properties

import com.fasterxml.jackson.databind.ObjectMapper
import models.Flight
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.Serializer

object FlightsInformationProducer {

  val topic = "cities"
  val brokers = "localhost:9092"

  val props = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
  props.put(ProducerConfig.CLIENT_ID_CONFIG, "producers.FlightsInformationProducer")
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "producers.FlightSerializer")
  val producer = new KafkaProducer[String, Flight](props)

  def addFlightInformationToKafka(source: String, flight: Flight): Unit = {
    //Keys are used to determine the partition within a log to which a message get's appended to
    val data = new ProducerRecord[String, Flight](topic, source, flight)
    producer.send(data)
    producer.close()
    print(data + "\n")
  }
}

class FlightSerializer extends Serializer[Flight] {

  val objectMapper = new ObjectMapper()

  def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {

  }


  def close(): Unit = {
  }

  override def serialize(topic: String, data: Flight): Array[Byte] = {
    try {
      objectMapper.writeValueAsBytes(data)
    } catch {
      case e: Exception => e.printStackTrace(); null
    }
  }
}
