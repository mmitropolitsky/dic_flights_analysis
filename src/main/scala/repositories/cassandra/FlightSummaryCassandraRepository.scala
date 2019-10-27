package repositories.cassandra

import com.datastax.driver.core.Cluster
import com.datastax.driver.core.querybuilder.QueryBuilder
import convertors.CassandraRowToFlightSummaryConverter
import models.FlightSummary

class FlightSummaryCassandraRepository {

  val cluster = Cluster.builder().withoutJMXReporting()
    .addContactPoint("127.0.0.1").build()
  val session = cluster.connect()

  session.execute("CREATE KEYSPACE IF NOT EXISTS weather WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };")

  session.execute(
    """CREATE TABLE IF NOT EXISTS
      weather.flight_summary (
      "airportCode" text,
      "date" text,
      "totalFlights" int,
      "totalLateFlights" int,
      "totalLateFlightsDueToWeather" int,
      PRIMARY KEY ("airportCode", "date"));""".stripMargin)


  def selectAll(): List[FlightSummary] = {
    if (session.isClosed) cluster.connect()
    val select = QueryBuilder.select(
      "airportCode",
      "date",
      "totalFlights",
      "totalLateFlights",
      "totalLateFlightsDueToWeather").from("weather", "flight_summary")

    val resultSet = session.execute(select)

    session.close()

    var list = List[FlightSummary]()
    val it = resultSet.iterator()
    while (it.hasNext) {
      val r = it.next()
      list = list :+ CassandraRowToFlightSummaryConverter.convert(r)
    }

    list
  }
}
