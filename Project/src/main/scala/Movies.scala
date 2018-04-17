package com.laschet.cliff.movierecommender.data

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.DataFrame

object Movies {
  import com.laschet.cliff.movierecommender.spark.LocalSparkContext._

  /**
    * Creates a map of all the movies referred to in the ratings training data. Used for console printing purposes.
    */
  def getAllMovies(): Map[Int, String] = {
    CSV.load("movies.csv").rdd.map(row => (row.getInt(0), row.getString(1))).collect().toMap
  }

  /**
    * Creates a data frame of the 10 most rated movies, ranked by the number of ratings they received in total.
    */
  def getMostFrequentlyRatedMovies(allMovies: Map[Int, String], rawPopulationRatings: DataFrame): Map[String, Int] = {
    rawPopulationRatings.rdd.map(row => (allMovies.get(row.getInt(1)).get, 1)).reduceByKey((f1, f2) => f1 + f2).collect().sortBy(-_._2).take(10).toMap
  }

  case class MovieRating(userId: Int, movieId: Int, movieTitle: String, rating: Double)

  /**
    * Asks the user to rate the 10 most rated movies using console input.
    */
  def getPersonalRatings(allMovies: Map[Int, String], rawPopulationRatings: DataFrame): DataFrame = {
    val mostFrequentlyRatedMovies = getMostFrequentlyRatedMovies(allMovies, rawPopulationRatings)

    println("Please rate the following movies between 1-5 (5 being the best movie ever):")
    val personalRatings = mostFrequentlyRatedMovies.map({ movie =>
      println(s"${movie._1}:")
      var success = false
      var rating = 0d

      while(!success) {
        try {
          rating = readDouble()
          success = true
        } catch {
          case exception: Any => {println(s"Wrong input, please rate ${movie._1}:")}
        }
      }

      val movieId = allMovies.find(entry => entry._2.equals(movie._1)).get._1
      MovieRating(0, movieId, movie._1, rating)
    })
    val parallelizedPersonalRatings: RDD[MovieRating] = sparkContext.parallelize(personalRatings.toSeq)
    sqlContext.createDataFrame(parallelizedPersonalRatings)
  }
}