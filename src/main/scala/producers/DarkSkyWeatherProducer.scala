package producers

import java.util.Properties

import org.apache.kafka.clients.producer.ProducerConfig


object DarkSkyWeatherProducer {

  val topic = "cities"
  val brokers = sys.env("KAFKA_BROKER")

  val props = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
  props.put(ProducerConfig.CLIENT_ID_CONFIG, "producers.DarkSkyWeatherProducer")
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "producers.FlightSerializer")


}
