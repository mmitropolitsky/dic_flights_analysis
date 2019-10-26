name := "flights_analysis"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.4.4",
  "org.apache.spark" %% "spark-sql" % "2.4.4",
  "org.apache.spark" % "spark-streaming_2.11" % "2.4.4",
  "org.apache.spark" % "spark-streaming-kafka-0-8_2.11" % "2.2.1",
  "com.datastax.spark" %% "spark-cassandra-connector-unshaded" % "2.4.1",
  //.exclude("io.netty", "netty-handler"),
  "com.datastax.dse" % "dse-java-driver-query-builder" % "2.2.1",
  //  "io.netty" % "netty-transport-native-epoll" % "4.1.6.Final" classifier "linux-x86_64",

  "com.lihaoyi" %% "requests" % "0.1.8",
  "net.liftweb" %% "lift-json" % "3.3.0"

)