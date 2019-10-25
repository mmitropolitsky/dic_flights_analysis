package serializers

import java.time.LocalDateTime

import net.liftweb.json._


class LocalDateTimeFromStringSerializer extends Serializer[LocalDateTime] {
  private val LocalDateTimeClass = classOf[LocalDateTime]

  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), LocalDateTime] = {
    case (TypeInfo(LocalDateTimeClass, _), json) => json match {

      case JString(dTF) => if (dTF.takeRight(1) == "Z") LocalDateTime.parse(dTF.dropRight(1))
      else LocalDateTime.parse(dTF);
      case x => throw new MappingException("Can't convert " + x + " to LocalDateTime")
    }
  }

  def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case x: LocalDateTime => JString(x.toString)
  }
}
