package com.laschet.cliff.movierecommender.data

import org.apache.spark.sql.{DataFrame, SQLContext}

/**
  * Created by Cliff Laschet on 2/25/2016.
  */
object CSV {

  def load(dir : String)(implicit sqlContext : SQLContext) : DataFrame = {
    sqlContext.read
      .format("com.databricks.spark.csv")
      .option("header", "true")
      .option("inferSchema", "true")
      .load(dir)
  }
}
