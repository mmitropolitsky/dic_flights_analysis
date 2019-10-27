package consumers

import org.apache.spark._
import org.apache.spark.sql._
import com.datastax.spark.connector.rdd.ReadConf

import com.datawizards.splot.api.implicits._

import models.FlightSummary
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.cassandra.DataFrameReaderWrapper

object VisualizeFlightsInformation {

  implicit val myObjEncoder = org.apache.spark.sql.Encoders.product[FlightSummary]

  def visualizeRatioBetweenAllFlightsAndLateFlights(): Unit = {

    val spark = SparkSession
      .builder()
       .master("local")
      .appName("FlightsInformationConsumer")
      .config("spark.cassandra.connection.host", "localhost")
      .getOrCreate()

    val flightsSummaryDF = spark
      .read
      .cassandraFormat("flight_summary", "weather")
//      .options(ReadConf.SplitSizeInMBParam.option(32))
      .load()


    val flightSummary = flightsSummaryDF.as(myObjEncoder)
    flightSummary.as

    Seq(
      ("lateFlights", flightSummary.),
      ("flightsOnTime", 72),
      ("FR", 63),
      ("UK", 62),
        ("IT", 61)
      )
      .plotPie()
  }

}
