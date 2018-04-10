name := "NetflixRecommendationSystem"

name := "movielens-als"

version := "0.1"

scalaVersion := "2.10.4"
val kafkaVersion = "1.1.0"

libraryDependencies += "org.apache.spark" % "spark-mllib_2.10" % "1.1.0"
libraryDependencies += "org.apache.kafka" % "kafka-clients" % kafkaVersion
resolvers += Resolver.mavenLocal