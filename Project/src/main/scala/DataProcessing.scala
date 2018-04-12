
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

object DataProcessing {

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Simple Application").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val format = new java.text.SimpleDateFormat("yyyy-dd-mm")
    val time = format.parse("2003-01-16").getTime()

    val NtflixRecosFile = "/Users/sonalichaudhari/Desktop/netflix-prize-data/train/"

    val file = new File("/Users/sonalichaudhari/Desktop/netflix-prize-data/train_data.csv")
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write("movieId,userId,rating,timestamp"+"\n")

    var train_files = Array("combined_data_1.txt","combined_data_2.txt","combined_data_3.txt","combined_data_4.txt")
    for ( i <- 0 to (train_files.length - 1)) {
      print(train_files(i))
      var app = ""
      for (line <- Source.fromFile(NtflixRecosFile+train_files(i)).getLines) {
        // app = line.toString()
        if (line.contains(":")) {
          //        println("printing for movie with id "+line)
          app = line.toString().stripSuffix(":")
        }
        else {
          //        println(app + "," + line)
          var entry = app+","+line
          bw.write(entry+"\n")
        }
      }
    }






    data.coalesce(1).saveAsTextFile("/Users/sonalichaudhari/Desktop/netflix-prize-data/data.txt")
    val data = sc.textFile("/Users/sonalichaudhari/Desktop/netflix-prize-data/train/*csv")

        val myRatings = loadRatings("/Users/sonalichaudhari/Desktop/netflix-prize-data/PersonalRatingsLatest.txt")
        val myRatingsRDD = sc.parallelize(myRatings, 1)
    //

    //
//    val ratings = sc.textFile(new File(NtflixRecosFile,"t1.csv").toString).map { line =>
//      val fields = line.split(",")
//          // format: (timestamp % 10, Rating(userId, movieId, rating))
//          (fields(1).toInt, fields(0).toInt,fields(2).toDouble,format.parse(fields(3)).getTime().toLong % 10)
//    }

//    ratings.collect().foreach(println)

    //    val movie_titles = "/Users/sonalichaudhari/Desktop/netflix-prize-data/"
    //
    //    val movies = sc.textFile(new File(movie_titles, "movie_titles.csv").toString).map { line =>
    //      val fields = line.split(",")
    //      // format: (movieId, movieName)
    //      (fields(0).toInt, fields(2))
    //    }.collect().toMap
    //
    //    val numRatings = ratings.count()
    //    val numUsers = ratings.map(_._2.user).distinct().count()
    //    val numMovies = ratings.map(_._2.product).distinct().count()
    //
    //
    //    println("Got " + numRatings + " ratings from "
    //      + numUsers + " users on " + numMovies + " movies.")



    //    val numPartitions = 4
    //    val training = ratings.filter(x => x._1 < 6)
    //      .values
    //      .union(myRatingsRDD)
    //      .repartition(numPartitions)
    //      .cache()
    //    val validation = ratings.filter(x => x._1 >= 6 && x._1 < 8)
    //      .values
    //      .repartition(numPartitions)
    //      .cache()
    //    val test = ratings.filter(x => x._1 >= 8).values.cache()
    //
    //    val numTraining = training.count()
    //    val numValidation = validation.count()
    //    val numTest = test.count()
    //
    //    println("Training: " + numTraining + ", validation: " + numValidation + ", test: " + numTest)
    //
    //    val ranks = List(8, 12)
    //    val lambdas = List(0.1, 10.0)
    //    val numIters = List(10, 20)
    //    var bestModel: Option[MatrixFactorizationModel] = None
    //    var bestValidationRmse = Double.MaxValue
    //    var bestRank = 0
    //    var bestLambda = -1.0
    //    var bestNumIter = -1
    //    for (rank <- ranks; lambda <- lambdas; numIter <- numIters) {
    //      val model = ALS.train(training, rank, numIter, lambda)
    //      val validationRmse = computeRmse(model, validation, numValidation)
    //      println("RMSE (validation) = " + validationRmse + " for the model trained with rank = "
    //        + rank + ", lambda = " + lambda + ", and numIter = " + numIter + ".")
    //      if (validationRmse < bestValidationRmse) {
    //        bestModel = Some(model)
    //        bestValidationRmse = validationRmse
    //        bestRank = rank
    //        bestLambda = lambda
    //        bestNumIter = numIter
    //      }
    //    }

    //
    //    println("The End")
    //
    //  }
    //
    //  /** Compute RMSE (Root Mean Squared Error). */
    //  def computeRmse(model: MatrixFactorizationModel, data: RDD[Rating], n: Long): Double = {
    //    val predictions: RDD[Rating] = model.predict(data.map(x => (x.user, x.product)))
    //    val predictionsAndRatings = predictions.map(x => ((x.user, x.product), x.rating))
    //      .join(data.map(x => ((x.user, x.product), x.rating)))
    //      .values
    //    math.sqrt(predictionsAndRatings.map(x => (x._1 - x._2) * (x._1 - x._2)).reduce(_ + _) / n)
    //  }
    //
    //  /** Load ratings from file. */
    //  def loadRatings(path: String): Seq[Rating] = {
    //    val lines = Source.fromFile(path).getLines()
    //    val ratings = lines.map { line =>
    //      val fields = line.split("::")
    //      Rating(fields(0).toInt, fields(1).toInt, fields(2).toDouble)
    //    }.filter(_.rating > 0.0)
    //    if (ratings.isEmpty) {
    //      sys.error("No ratings provided.")
    //    } else {
    //      ratings.toSeq
    //    }
    //  }

  }
}