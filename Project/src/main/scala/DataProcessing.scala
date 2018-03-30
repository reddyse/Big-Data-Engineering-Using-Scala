
import java.io.File

import scala.io.Source


import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd._
import org.apache.spark.mllib.recommendation.{ALS, Rating, MatrixFactorizationModel}

object DataProcessing {

  def main(args: Array[String]) {

    val conf = new SparkConf()
      .setAppName("NetflixRecosALS")
      .setMaster("local[2]")
      .set("spark.executor.memory", "2g")
    val sc = new SparkContext(conf)

    //val myRatings = loadRatings("")
    //val myRatingsRDD = sc.parallelize(myRatings, 1)

    val movieLensHomeDir = "/Users/sonalichaudhari/Desktop/Project/Rating.csv"

    val ratings = sc.textFile(new File(movieLensHomeDir).toString).map { line =>
      val fields = line.split(",")
      // format: (timestamp % 10, Rating(userId, movieId, rating))
      //(fields(3).toLong % 10, Rating(fields(0).toInt, fields(1).toInt, fields(2).toDouble))
      (fields(1).toLong % 10)
    }

    /** Load ratings from file. */
    def loadRatings(path: String): Seq[Rating] = {
      val lines = Source.fromFile(path).getLines()
      val ratings = lines.map { line =>
        val fields = line.split("::")
        Rating(fields(0).toInt, fields(1).toInt, fields(2).toDouble)
      }.filter(_.rating > 0.0)
      if (ratings.isEmpty) {
        sys.error("No ratings provided.")
      } else {
        ratings.toSeq
      }

    }
  }
}