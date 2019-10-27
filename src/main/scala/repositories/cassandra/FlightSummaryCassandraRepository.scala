package repositories.cassandra

import com.datastax.driver.core.Cluster

class FlightSummaryCassandraRepository {

  val cluster = Cluster.builder().withoutJMXReporting()
    .addContactPoint("127.0.0.1").build()
  val session = cluster.connect()

  session.execute("CREATE KEYSPACE IF NOT EXISTS weather WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };")

  //  airportCode: String,
  //  totalFlights: Integer,
  //  totalLateFlights: Integer,
  //  lateFlights: List[FlatFlight],
  //  totalLateFlightsDueToRain: Integer,
  //  totalLateFlightsDueToVisibility: Integer,
  //  totalLateFlightsDueToWind: Integer

  session.execute(
    """CREATE TABLE IF NOT EXISTS
      weather.flight_summary (
      airportCode text PRIMARY KEY,
      totalFlights int,
      totalLateFlights int,
      totalLateFlightsDueToWeather int);""".stripMargin)


}
