package convertors

import java.time.{LocalDateTime, ZoneOffset}

import com.datastax.driver.core.Row
import models.FlatDailyWeather

object CassandraRowToFlatWeatherConverter {

  def convert(row: Row): FlatDailyWeather = {
    FlatDailyWeather(row.getString("airportDateKey"),
      row.getString("airportCode"),
      LocalDateTime.ofInstant(row.getTimestamp("time").toInstant, ZoneOffset.UTC),
      row.getString("summary"),
      row.getString("icon"),
      LocalDateTime.ofInstant(row.getTimestamp("sunriseTime").toInstant, ZoneOffset.UTC),
      LocalDateTime.ofInstant(row.getTimestamp("sunsetTime").toInstant, ZoneOffset.UTC),
      row.getFloat("precipIntensityMax"),
      row.getFloat("precipProbability"),
      row.getFloat("temperatureMin"),
      row.getFloat("temperatureMax"),
      row.getFloat("apparentTemperatureMin"),
      row.getFloat("apparentTemperatureMax"),
      row.getFloat("windSpeed"),
      row.getFloat("cloudCover"),
      row.getFloat("pressure"),
      row.getFloat("visibility")
    )
  }


}
