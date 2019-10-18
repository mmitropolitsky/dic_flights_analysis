name := "flights_analysis"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.2.1",
  "org.apache.spark" %% "spark-sql" % "2.2.1",
  "org.apache.spark" % "spark-streaming_2.11" % "2.2.1",
  "org.apache.spark" % "spark-streaming-kafka-0-8_2.11" % "2.2.1",
  ("com.datastax.spark" %% "spark-cassandra-connector" % "2.0.2").exclude("io.netty", "netty-handler"),
//  "com.lihaoyi" %% "ujson" % "0.7.1",
  "com.lihaoyi" %% "requests" % "0.1.8",
//  "com.typesafe.play" % "play-json_2.11" % "2.7.3",
  "net.liftweb" %% "lift-json" % "3.3.0"

)