package edu.neu.netflix

import java.util.{Properties, Random}
import java.util.concurrent.Future

import org.apache.kafka.clients.producer.{KafkaProducer, RecordMetadata}
import org.apache.kafka.clients.producer.ProducerRecord

object KafkaProd extends App {

  override def main(args: Array[String]) = {

    val topic = "test"
    println(s"Connecting to $topic")
    val rnd = new Random()
    val props = new Properties()

    props.put("bootstrap.servers", "localhost:9092")
    props.put("client.id", "KafkaProducer")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")


    val producer = new KafkaProducer[String, String](props)
    val t = System.currentTimeMillis()
    val data = new ProducerRecord[String, String](topic, "testkey", "This is Praneeth's sample message")

    producer.send(data)
    producer.close()

  }
}
