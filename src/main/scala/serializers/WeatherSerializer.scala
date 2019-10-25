package serializers

import java.util

import com.fasterxml.jackson.databind.ObjectMapper
import models.Weather
import org.apache.kafka.common.serialization.Serializer

class WeatherSerializer extends Serializer[Weather] {

  val objectMapper = new ObjectMapper()

  def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {

  }


  def close(): Unit = {
  }

  override def serialize(topic: String, data: Weather): Array[Byte] = {
    try {
      objectMapper.writeValueAsBytes(data)
    } catch {
      case e: Exception => e.printStackTrace(); null
    }
  }
}
