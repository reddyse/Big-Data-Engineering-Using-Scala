package edu.neu.netflix

import java.util
import java.util.{Properties, Random}

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.junit.{Assert, Test}
import org.scalatest.junit.JUnitSuite

class TestKafkaCons extends JUnitSuite {

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

  val propsCons = new Properties()
  propsCons.put("bootstrap.servers", "localhost:9092")
  propsCons.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  propsCons.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  propsCons.put("group.id", "something2")
  val consumer = new KafkaConsumer[String, String](propsCons)


    @Test
  def testKafkaConsumer() {

      consumer.subscribe(util.Collections.singletonList(topic))
      val records = consumer.poll(1)
      Assert.assertTrue(records.isEmpty)

    }

  @Test
  def checkConsumerMetrics()
  {
    Assert.assertTrue(consumer.metrics(  ).size()>0)

  }
  @Test
  def checkConsumerTransaction()
  {
    consumer.subscribe(util.Collections.singletonList(topic))
    consumer.paused()
    consumer.close()
    Assert.assertTrue(true)

  }


  @Test
  def checkConsumerTopicsList()
  {
    Assert.assertFalse(consumer.listTopics().isEmpty)

  }


  @Test
  def checkConsumerClose()
  {
    consumer.close()
    Assert.assertTrue(true)

  }
  @Test
  def checkConsumerSubscriptionSize()
  {
    Assert.assertTrue(consumer.subscription().size()==0)

  }

  @Test
  def checkConsumerSubscriptionStrategy()
  {
    Assert.assertTrue(consumer.subscription().isEmpty)

  }







}
