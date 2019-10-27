package consumers

import com.datastax.driver.core.Cluster
import convertors.CassandraRowToFlatWeatherConverter
import models.{FlatDailyWeather, FlatFlight}
import org.apache.spark.SparkContext
import org.apache.spark.streaming.dstream.DStream
import repositories.cassandra.WeatherCassandraRepository

object FlightWeatherMerger {

  val cluster = Cluster.builder().withoutJMXReporting()
    .addContactPoint("127.0.0.1").build()


  def joinFlightsWithWeatherDate(sc: SparkContext, flights: DStream[(String, FlatFlight)]): DStream[(String, (FlatFlight, FlatDailyWeather))] = {

    val repo = new WeatherCassandraRepository
    val resultSet = repo.selectAll()

    var list = Seq[FlatDailyWeather]()
    val it = resultSet.iterator()
    while (it.hasNext) {
      val r = it.next()
      list = list :+ CassandraRowToFlatWeatherConverter.convert(r)
    }

    val cassandraRowRdd = sc.parallelize(list)

    val dailyWeatherRdd = cassandraRowRdd.map(flatDailyWeather => {
      (flatDailyWeather.airportDateKey, flatDailyWeather)
    })

    flights.transform(_.join(dailyWeatherRdd))
  }


}
