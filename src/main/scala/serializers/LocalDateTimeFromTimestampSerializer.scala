package serializers

import java.time.{Instant, LocalDateTime, ZoneId}

import net.liftweb.json._

class LocalDateTimeFromTimestampSerializer extends Serializer[LocalDateTime] {
  private val LocalDateTimeClass = classOf[LocalDateTime]

  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), LocalDateTime] = {
    case (TypeInfo(LocalDateTimeClass, _), json) => json match {

      case JInt(dTF) => LocalDateTime.ofInstant(Instant.ofEpochSecond(dTF.longValue()), ZoneId.systemDefault)
      case x => throw new MappingException("Can't convert " + x + " to LocalDateTime")
    }
  }

  override def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case x: LocalDateTime => JString(x.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli().toString())
  }
}
