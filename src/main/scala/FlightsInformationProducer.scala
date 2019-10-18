import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

class FlightsInformationProducer {

  val alphabet = 'a' to 'z'
  val events = 10000
  val topic = "avg"
  val brokers = "localhost:9092"

  val props = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
  props.put(ProducerConfig.CLIENT_ID_CONFIG, "FlightsInformationProducer")
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
  val producer = new KafkaProducer[String, String](props)

  while (true) {
    val data = new ProducerRecord[String, String](topic, "key", "value")
    producer.send(data)
    print(data + "\n")
  }

  producer.close()
}
