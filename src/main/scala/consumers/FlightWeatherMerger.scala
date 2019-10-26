package consumers

import com.datastax.spark.connector._
import org.apache.spark.sql.cassandra._
import convertors.CassandraRowToFlatWeatherConverter
import models.{FlatDailyWeather, FlatFlight}
import org.apache.spark.SparkContext
import org.apache.spark.streaming.dstream.DStream

object FlightWeatherMerger {

  def joinFlightsWithWeatherDate(sc: SparkContext, flights: DStream[(String, FlatFlight)]): DStream[(String, (FlatFlight, FlatDailyWeather))] = {
    val cassandraRowRdd = sc.cassandraTable("weather", "daily_data")

    println(cassandraRowRdd.count)

    val dailyWeatherRdd = cassandraRowRdd.map(row => {
      val flatDailyWeather = CassandraRowToFlatWeatherConverter.convert(row)
      (flatDailyWeather.airportDateKey, flatDailyWeather)
    })


    val transformedDStream = flights.transform(flightRdd => {
      flightRdd.join(dailyWeatherRdd)
    })

    transformedDStream
  }

}
