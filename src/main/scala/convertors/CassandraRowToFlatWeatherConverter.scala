package convertors

import java.time.{LocalDateTime, ZoneOffset}

import com.datastax.spark.connector.CassandraRow
import models.FlatDailyWeather

object CassandraRowToFlatWeatherConverter {

  def convert(row: CassandraRow): FlatDailyWeather = {
    FlatDailyWeather(row.getString("airportDateKey"),
      row.getString("airportCode"),
      LocalDateTime.ofInstant(row.getDate("time").toInstant, ZoneOffset.UTC),
      row.getString("summary"),
      row.getString("icon"),
      LocalDateTime.ofInstant(row.getDate("sunriseTime").toInstant, ZoneOffset.UTC),
      LocalDateTime.ofInstant(row.getDate("sunsetTime").toInstant, ZoneOffset.UTC),
      row.getDouble("precipIntensityMax"),
      row.getDouble("precipProbability"),
      row.getDouble("temperatureMin"),
      row.getDouble("temperatureMax"),
      row.getDouble("apparentTemperatureMin"),
      row.getDouble("apparentTemperatureMax"),
      row.getDouble("windSpeed"),
      row.getDouble("cloudCover"),
      row.getDouble("pressure")
    )
  }


}
