package consumers.deserializer

import java.io.{ByteArrayInputStream, ObjectInputStream}

import kafka.serializer.Decoder
import kafka.utils.VerifiableProperties
import models.FlatFlight

class FlightDeserializer(props: VerifiableProperties = null) extends Decoder[FlatFlight] {

  override def fromBytes(bytes: Array[Byte]): FlatFlight = {
    val byteIn = new ByteArrayInputStream(bytes)
    val objIn = new ObjectInputStream(byteIn)
    val obj = objIn.readObject().asInstanceOf[FlatFlight]
    byteIn.close()
    objIn.close()
    obj
  }
}