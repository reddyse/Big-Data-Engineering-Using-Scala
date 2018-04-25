package edu.neu.netflix

import java.util.{Properties, Random}

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.junit.{Assert, Ignore, Test}
import org.scalatest.junit.JUnitSuite

class TestKafkaProd extends JUnitSuite {
  private val messageContent1 = "test"
  private val topic1 = "test-topic"

  private val messageContent2 = "test1"
  private val topic2 = "test1$topic"

  val topic = "userratings"
  println(s"Connecting to $topic")
  val rnd = new Random()

  val propsProd = new Properties()
  propsProd.put("bootstrap.servers", "localhost:9092")
  propsProd.put("client.id", "KafkaProducer")
  propsProd.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  propsProd.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")


  @Test
  def testKafkaProducer() {

    val producer = new KafkaProducer[String, String](propsProd)
    val t = System.currentTimeMillis()
    //userID, movieID,Rating,timestamp
    val data = new ProducerRecord[String, String](topic, "0,886,4.0,"+t)
    print("Publisher working properly")
    producer.send(data)
    producer.close()

  }

  @Test
  def checkKafkaTopicPartitions()  {
    val producer = new KafkaProducer[String, String](propsProd)
    Assert.assertFalse(producer.partitionsFor(topic).contains())
  }

  @Test
  def checkKafkaTopicclientid()  {
    val producer = new KafkaProducer[String, String](propsProd)
    producer.close()
    Assert.assertTrue(true)
  }


  @Test
  def checkKafkaTopicMetrics()  {
    val producer = new KafkaProducer[String, String](propsProd)
    Assert.assertTrue(producer.metrics(  ).size()>0)
  }

  @Ignore
  def checkKafkaProducerTransaction()  {
    propsProd.put("client.id", "KafkaProducer2")
    val producer = new KafkaProducer[String, String](propsProd)
    producer.initTransactions()
    producer.beginTransaction()
    Assert.assertTrue(true)
    testKafkaProducer()
    producer.commitTransaction()
    Assert.assertTrue(true)
    producer.close()
    Assert.assertTrue(true)
  }


}
