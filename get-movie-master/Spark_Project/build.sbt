name := "Spark_Project"

version := "1.0"

scalaVersion := "2.10.4"
val kafkaVersion = "1.1.0"

libraryDependencies += "org.apache.spark" % "spark-mllib_2.10" % "1.1.0"
libraryDependencies += "org.apache.kafka" % "kafka-clients" % kafkaVersion
resolvers += Resolver.mavenLocal


/*
scalaVersion := "2.10.4"

val spark = "org.apache.spark"
val sparkVersion = "1.5.1"

// Change this to another test framework if you prefer
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"
libraryDependencies += spark %% "spark-core" % sparkVersion % "provided"
libraryDependencies += spark %% "spark-mllib" % sparkVersion % "provided"
// https://mvnrepository.com/artifact/org.apache.spark/spark-sql_2.10
libraryDependencies += spark % "spark-sql_2.10" % "1.5.2"

// Uncomment to use Akka
//libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.11"
*/

/*scalaVersion := "2.11.6"
libraryDependencies ++= Seq(
"org.scalatest" %% "scalatest" % "2.2.4" % "test",
"org.apache.spark" % "spark-core_2.11" % "1.6.1",
"org.apache.spark" % "spark-mllib_2.11" % "1.6.1"
)
// https://mvnrepository.com/artifact/com.databricks/spark-csv_2.10
libraryDependencies += "com.databricks" % "spark-csv_2.10" % "1.5.0"
// https://mvnrepository.com/artifact/org.deeplearning4j/dl4j-spark_2.11
libraryDependencies ++= Seq(
  "jfree" % "jfreechart" % "1.0.13",
  "commons-io" % "commons-io" % "2.4",
  "com.google.guava" % "guava" % "19.0",
  "jfree" % "jfreechart" % "1.0.13",
  "org.bytedeco" % "javacv" % "1.2",
  "org.datavec" % "datavec-data-codec" % "0.5.0",
  "org.deeplearning4j" % "deeplearning4j-core" % "0.5.0",
  "org.deeplearning4j" % "deeplearning4j-nlp" % "0.5.0",
  "org.deeplearning4j" % "deeplearning4j-ui" % "0.5.0",
  "org.jblas" % "jblas" % "1.2.4",
  "mysql" % "mysql-connector-java" % "5.1.12"
  //"org.nd4j" % "nd4j-native-platform" % "0.5.0"
   //"org.nd4j" % "nd4j-native" % "0.5.0" classifier "$platform",
    //"org.nd4j" % "nd4j-native" % "0.5.0"
)
// https://mvnrepository.com/artifact/org.springframework/spring-core
libraryDependencies += "org.springframework" % "spring-core" % "4.3.3.RELEASE"
resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies += "org.nd4j" % "nd4j-native" % "0.7.1"
libraryDependencies += "org.nd4j" % "nd4j-native-platform" % "0.7.1"*/







