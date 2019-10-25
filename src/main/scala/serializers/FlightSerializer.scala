package serializers

import java.util

import com.fasterxml.jackson.databind.ObjectMapper
import models.Flight
import org.apache.kafka.common.serialization.Serializer

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
