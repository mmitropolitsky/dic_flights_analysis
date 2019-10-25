package consumers.deserializer

import java.io.{ByteArrayInputStream, ObjectInputStream}

import kafka.serializer.Decoder
import kafka.utils.VerifiableProperties
import models.Flight

class FlightDeserializer(props: VerifiableProperties = null) extends Decoder[Flight] {

  override def fromBytes(bytes: Array[Byte]): Flight = {
    val byteIn = new ByteArrayInputStream(bytes)
    val objIn = new ObjectInputStream(byteIn)
    val obj = objIn.readObject().asInstanceOf[Flight]
    byteIn.close()
    objIn.close()
    obj
  }
}