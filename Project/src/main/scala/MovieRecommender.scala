package com.laschet.cliff.movierecommender
import java.io.File

import scala.io.Source

import org.apache.log4j.Logger
import org.apache.log4j.Level

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd._
import org.apache.spark.mllib.recommendation.{ALS, Rating, MatrixFactorizationModel}

import java.io.File

import com.laschet.cliff.movierecommender.data.{CSV, Movies}
import com.laschet.cliff.movierecommender.spark.LocalSparkContext
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.recommendation.{ALS, Rating}

import java.io.File

import scala.io.Source._
import scala.io.Source

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd._
import org.apache.spark.mllib.recommendation.{ALS, Rating, MatrixFactorizationModel}

import java.io.{BufferedWriter, FileWriter}
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.util.Random
import au.com.bytecode.opencsv.CSVWriter

/**
  * Demo of using setting up a basic machine learning flow for recommending movies based on your personal preferences,
  * using the collaborative filtering machine learning technique of Apache Spark. The default 'recipe' for solving the
  * machine learning problem is used:
  * A) Gathering raw data.
  * B) Pre-processing it to training data.
  * C) Training the ML technique using the training data.
  * D) Recommending movies using the obtained ML model and a small set of personal ratings.
  *
  * Created by Cliff Laschet on 2/25/2016.
  */
object MovieRecommender {

  def main(args: Array[String]) ={
    //Setup
    import LocalSparkContext._
    //-Set logging level
    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)


//    val conf = new SparkConf().setAppName("Simple Application").setMaster("local[*]")
//    val sc = new SparkContext(conf)
//
//    val format = new java.text.SimpleDateFormat("yyyy-dd-mm")
//    val time = format.parse("2003-01-16").getTime()
//
//    val NtflixRecosFile = "/Users/sonalichaudhari/Desktop/netflix-prize-data/train/"
//
//    val file = new File("/Users/sonalichaudhari/Desktop/netflix-prize-data/ratings.csv")
//    val bw = new BufferedWriter(new FileWriter(file))
//    bw.write("movieId,userId,rating,timestamp"+"\n")
//
//    var train_files = Array("combined_data_1.txt","combined_data_2.txt","combined_data_3.txt","combined_data_4.txt")
//    for ( i <- 0 to (train_files.length - 1)) {
//      print(train_files(i))
//      var app = ""
//      for (line <- Source.fromFile(NtflixRecosFile+train_files(i)).getLines) {
//        // app = line.toString()
//        if (line.contains(":")) {
//          //        println("printing for movie with id "+line)
//          app = line.toString().stripSuffix(":")
//        }
//        else {
//          //        println(app + "," + line)
//          var entry = app+","+line
//          bw.write(entry+"\n")
//        }
//      }
//    }

    //A. Load data
    //1-Retrieve all the movies ever rated (not needed by machine learning technique,
    //  but convenient for printing recommendations later).
    val allMovies = Movies.getAllMovies()

    //2-Retrieve all the ratings from the complete population of people.
    val rawPopulationRatings = CSV.load("ratings.csv")
    //println(rawPopulationRatings)

    //3-Retrieve personal ratings from the user for the most frequently rated movies.
    val rawPersonalRatings = Movies.getPersonalRatings(allMovies, rawPopulationRatings)
    rawPersonalRatings.collect().foreach(println)


    //B. Pre-processing
    //1-Drop columns in the dataset that we do not need for our ML case.
    val preProcessedPopulationRatings = rawPopulationRatings.drop("timestamp")
    val preProcessedPersonalRatings = rawPersonalRatings.drop("movieTitle")

    //2-Merge the population and personal ratings into one dataset.
    val unionData = preProcessedPopulationRatings.unionAll(preProcessedPersonalRatings)

    //3-Transform the joined data to a format that can be used by our ML technique.
    val trainingData = unionData.rdd.map(row => Rating(row.getInt(0), row.getInt(1), row.getDouble(2)))

//     val Array(training, test) = trainingData.randomSplit(Array[Double](0.7, 0.3), 18)

//    training.cache()
//    test.cache()

//    val traincount = training.count()
//    val testcount = test.count()

//    println("training:"+traincount+"  test:"+testcount)

    //C. Training the ML technique, using the training data as input.
    //1-Set some parameters of the ML technique (out of scope for this demo).
    val rank = 8
    val iterations = 25
    val regularizationFactor = 10

    //2-Provide the training data and parameters as input for the ML technique.
    //  In this case, collaborative filtering is used, which is often taken as
    //  a basis for recommendation systems (e.g. Netflix).
    val model = ALS.train(trainingData, rank, iterations,regularizationFactor )

    val pred_input = trainingData.map { case Rating(user, product, rate) =>
      (user, product)
    }

    // pred_input.collect().foreach(println)

    val predictions =
      model.predict(pred_input).map { case Rating(user, product, rate) =>
        ((user, product), rate)
      }

   // predictions.collect().foreach(println)


//    val ratesAndPreds =tra.map { case Rating(user, product, rate) =>
//      ((user, product), rate)
//    }.join(predictions)


    //D. Perform recommendation based on my individual interests, using my personal ratings as input.
    //1-Predict my
    // interest for each movie, pick the top 25 movies with the highest predicted interest.
    val recommendations = model.recommendProducts(0, 10)

    //2-Print results
    var i = 1
    println("Movies recommended for you, based on your previous ratings:")
    recommendations.foreach { recommendation =>
      println(s"#$i: ${allMovies.get(recommendation.product).get} (expecting a rating of ${recommendation.rating})")
      i += 1
    }

//    val MSE = ratesAndPreds.map { case ((user, product), (r1, r2)) =>
//      val err = (r1 - r2)
//      err * err
//    }.mean()
//    println("Mean Squared Error = " + MSE)

    //Cleanup
    //-Stop Spark
    sparkContext.stop()
  }
}
