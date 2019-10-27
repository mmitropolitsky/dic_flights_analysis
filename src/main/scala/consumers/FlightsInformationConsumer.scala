package consumers

import com.datastax.driver.core.Cluster
import consumers.deserializer.FlightDeserializer
import convertors.CassandraRowToFlatWeatherConverter
import kafka.serializer.StringDecoder
import models.{FlatDailyWeather, FlatFlight}
import org.apache.spark.streaming._
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.{SparkConf, SparkContext}
import repositories.cassandra.WeatherCassandraRepository

object FlightsInformationConsumer extends App {


  // SPARK CONF
  val conf = new SparkConf()
    .setMaster("local[1]")
    .setAppName("FlightsInformationConsumer")
  val sc = new SparkContext(conf)
  val ssc = new StreamingContext(sc, Seconds(5))
  ssc.checkpoint("checkpoints")

  // CASSANDRA CONF
  val cluster = Cluster.builder().withoutJMXReporting()
    .addContactPoint("127.0.0.1").build()

  val session = cluster.connect()
  // KAFKA CONF
  val kafkaConf = Map(
    "metadata.broker.list" -> sys.env("KAFKA_BROKER"),
    "zookeeper.connect" -> sys.env("ZOOKEEPER_ADDRESS"),
    "group.id" -> "kafka-spark-streaming",
    "zookeeper.connection.timeout.ms" -> "1000")
  val topic = Set("cities")


  val flights = KafkaUtils.createDirectStream[
    String, FlatFlight,
    StringDecoder,
    FlightDeserializer](
    ssc, kafkaConf, topic)

  // TODO map with state calculate total flights / late flights below

  FlightCalculator.calculateTotalFlights(flights)

  val lateFlights = flights.filter(flight => {
    java.time.Duration.between(flight._2.arrivalActualTimeLocal, flight._2.arrivalExpectedTimeLocal).getSeconds > 30 * 60
  })

  val flightsWithWeather = FlightWeatherMerger.joinFlightsWithWeatherDate(sc, lateFlights)
  flightsWithWeather.count().print()

  FlightCalculator.calculateLateFlights(flightsWithWeather)

  val dangerousWeatherStates = List("rain", "fog", "wind", "snow", "sleet", "hail")

  val flightsDelayedDueToWeather = flightsWithWeather.filter(flight => {
    val weatherInfo = flight._2._2
    dangerousWeatherStates.contains(weatherInfo.icon) || weatherInfo.windSpeed > 4 ||
      weatherInfo.visibility < 8 || weatherInfo.precipIntensityMax > 4
  })

  // TODO filtered by weather values should be updated in the state
  ssc.start()
  ssc.awaitTermination()

  session.close()


  def joinFlightsWithWeatherDate(flights: DStream[(String, FlatFlight)]): DStream[(String, (FlatFlight, FlatDailyWeather))] = {

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




