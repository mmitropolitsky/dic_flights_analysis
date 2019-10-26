package repositories.cassandra

import com.datastax.driver.core.querybuilder.{Insert, QueryBuilder}
import com.datastax.driver.core.{BatchStatement, Cluster}
import convertors.ConverterUtils
import models.FlatDailyWeather


class WeatherCassandraRepository {

  val cluster = Cluster.builder().withoutJMXReporting()
    .addContactPoint("127.0.0.1").build()
  val session = cluster.connect()

  session.execute("CREATE KEYSPACE IF NOT EXISTS weather WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };")

  session.execute(
    """CREATE TABLE IF NOT EXISTS
      weather.alert (
      airportCode text PRIMARY KEY,
      description text,
      title text,
      time timestamp,
      expires timestamp,
      severity text);""".stripMargin)

  session.execute(
    """CREATE TABLE IF NOT EXISTS
      weather.daily_data (
      airportDataKey text PRIMARY KEY,
      airportCode text,
      time timestamp,
      summary text,
      icon text,
      sunriseTime timestamp,
      sunsetTime timestamp,
      precipIntensityMax float,
      precipProbability float,
      temperatureMin float,
      temperatureMax float,
      apparentTemperatureMin float,
      apparentTemperatureMax float,
      windSpeed float,
      cloudCover float,
      pressure float);""".stripMargin)

  def batchSaveFlatWeatherList(flatDailyWeatherList: List[FlatDailyWeather]): Unit = {
    if (session.isClosed) cluster.connect()

    var insertList: List[Insert] = List[Insert]()

    for (flatDailyWeather <- flatDailyWeatherList) {
      val insert = QueryBuilder.insertInto("weather", "daily_data")
        .value("airportDataKey", flatDailyWeather.airportDateKey)
        .value("airportCode", flatDailyWeather.airportCode)
        .value("time", ConverterUtils.convertLocalDateTimeToTimestamp(flatDailyWeather.time))
        .value("summary", flatDailyWeather.summary)
        .value("icon", flatDailyWeather.icon)
        .value("sunriseTime", ConverterUtils.convertLocalDateTimeToTimestamp(flatDailyWeather.sunriseTime))
        .value("sunsetTime", ConverterUtils.convertLocalDateTimeToTimestamp(flatDailyWeather.sunsetTime))
        .value("precipIntensityMax", flatDailyWeather.precipIntensityMax)
        .value("precipProbability", flatDailyWeather.precipProbability)
        .value("temperatureMin", flatDailyWeather.temperatureMin)
        .value("temperatureMax", flatDailyWeather.temperatureMax)
        .value("apparentTemperatureMin", flatDailyWeather.apparentTemperatureMin)
        .value("apparentTemperatureMax", flatDailyWeather.apparentTemperatureMax)
        .value("windSpeed", flatDailyWeather.windSpeed)
        .value("cloudCover", flatDailyWeather.cloudCover)
        .value("pressure", flatDailyWeather.pressure)

      insertList = insert :: insertList
    }

    val batchStatement = new BatchStatement(BatchStatement.Type.UNLOGGED)
    insertList.foreach(batchStatement.add(_))

    session.execute(batchStatement)

    session.close()

  }

  def selectAll(): Unit = {
    if (session.isClosed) cluster.connect()
    try {
      val select = QueryBuilder.select(
        "airportCode",
        "time",
        "summary",
        "icon",
        "sunriseTime",
        "sunsetTime",
        "precipIntensityMax",
        "precipProbability",
        "temperatureMin",
        "temperatureMax",
        "apparentTemperatureMin",
        "apparentTemperatureMax",
        "windSpeed",
        "cloudCover",
        "pressure").from("weather", "daily_data")

      val resultSet = session.execute(select)


    } finally {
      session.close()
    }

  }

  def saveAlertObject() {}
}
