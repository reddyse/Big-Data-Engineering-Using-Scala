package com.laschet.cliff.movierecommender.spark

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}


object LocalSparkContext {
  private val localSparkContext = new LocalSparkContext

  def sparkConfig = localSparkContext.sparkConfig
  def sparkContext = localSparkContext.sparkContext
  implicit def sqlContext = localSparkContext.sqlContext
}

class LocalSparkContext private {
  val sparkConfig = new SparkConf().setAppName("MovieRecommender").setMaster("local[4]")
  val sparkContext = new SparkContext(sparkConfig)
  val hadoopConfig = sparkContext.hadoopConfiguration
  hadoopConfig.set("fs.file.impl", classOf[org.apache.hadoop.fs.LocalFileSystem].getName)
  val sqlContext = new SQLContext(sparkContext)
}
