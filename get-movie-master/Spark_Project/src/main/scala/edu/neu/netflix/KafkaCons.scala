package edu.neu.netflix

import java.util
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.util.Properties
import scala.collection.JavaConverters._

object KafkaCons {
  def main(args: Array[String]) = {

    val TOPIC = "test"

    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")

    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("group.id", "something")

    val consumer = new KafkaConsumer[String, String](props)

    consumer.subscribe(util.Collections.singletonList(TOPIC))

    while (true) {
      val records = consumer.poll(100)
      for (record <- records.asScala) {
        println(record)
      }
    }
  }
}
