package producers

import java.util.Properties

import models.Weather
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}


object DarkSkyWeatherProducer {

  val topic = "cities"
  val brokers = sys.env("KAFKA_BROKER")

  val props = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
  props.put(ProducerConfig.CLIENT_ID_CONFIG, "producers.DarkSkyWeatherProducer")
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "serializers.WeatherSerializer")

  val producer = new KafkaProducer[String, Weather](props)

  def addWeatherInformationToKafka(source: String, flight: Weather): Unit = {
    //Keys are used to determine the partition within a log to which a message get's appended to
    val data = new ProducerRecord[String, Weather](topic, source, flight)
    producer.send(data)
    producer.close()
    print(data + "\n")
  }

}
