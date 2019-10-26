package serializers

import java.io.{ByteArrayOutputStream, ObjectOutputStream}
import java.util

import models.FlatFlight
import org.apache.kafka.common.serialization.Serializer

class FlightSerializer extends Serializer[FlatFlight] {

  def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {

  }


  def close(): Unit = {
  }

  override def serialize(topic: String, data: FlatFlight): Array[Byte] = {
    try {
      val byteOut = new ByteArrayOutputStream()
      val objOut = new ObjectOutputStream(byteOut)
      objOut.writeObject(data)
      objOut.close()
      byteOut.close()
      byteOut.toByteArray
    }
    catch {
      case ex: Exception => throw new Exception(ex.getMessage)
    }
  }
}
