#!/bin/bash
zookeeper-server-start.sh -daemon $KAFKA_HOME/config/zookeeper.properties
sleep 5
kafka-server-start.sh -daemon $KAFKA_HOME/config/server.properties
sleep 5
kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic cities
kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic weather
sleep 5
#cassandra -f