package com.laschet.cliff.movierecommender

import java.io.File

import com.laschet.cliff.movierecommender.data.{CSV, Movies}
import com.laschet.cliff.movierecommender.spark.LocalSparkContext
import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.recommendation.{ALS, Rating}

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

    //A. Load data
    //1-Retrieve all the movies ever rated (not needed by machine learning technique,
    //  but convenient for printing recommendations later).
    val allMovies = Movies.getAllMovies()

    //2-Retrieve all the ratings from the complete population of people.
    val rawPopulationRatings = CSV.load("ratings.csv")
    println(rawPopulationRatings)

    //3-Retrieve personal ratings from the user for the most frequently rated movies.
    val rawPersonalRatings = Movies.getPersonalRatings(allMovies, rawPopulationRatings)



    //B. Pre-processing
    //1-Drop columns in the dataset that we do not need for our ML case.
    val preProcessedPopulationRatings = rawPopulationRatings.drop("timestamp")
    val preProcessedPersonalRatings = rawPersonalRatings.drop("movieTitle")

    //2-Merge the population and personal ratings into one dataset.
    val unionData = preProcessedPopulationRatings.unionAll(preProcessedPersonalRatings)

    //3-Transform the joined data to a format that can be used by our ML technique.
    val trainingData = unionData.rdd.map(row => Rating(row.getInt(0), row.getInt(1), row.getDouble(2)))



    //C. Training the ML technique, using the training data as input.
    //1-Set some parameters of the ML technique (out of scope for this demo).
    val rank = 8
    val iterations = 25
    val regularizationFactor = 10

    //2-Provide the training data and parameters as input for the ML technique.
    //  In this case, collaborative filtering is used, which is often taken as
    //  a basis for recommendation systems (e.g. Netflix).
    val model = ALS.train(trainingData, rank, iterations, regularizationFactor)



    //D. Perform recommendation based on my individual interests, using my personal ratings as input.
    //1-Predict my interest for each movie, pick the top 25 movies with the highest predicted interest.
    val recommendations = model.recommendProducts(0, 25)

    //2-Print results
    var i = 1
    println("Movies recommended for you, based on your previous ratings:")
    recommendations.foreach { recommendation =>
      println(s"#$i: ${allMovies.get(recommendation.product).get} (expecting a rating of ${recommendation.rating})")
      i += 1
    }


    //Cleanup
    //-Stop Spark
    sparkContext.stop()
  }
}
