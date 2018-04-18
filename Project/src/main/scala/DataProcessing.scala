
import java.io.File

import scala.io.Source
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd._
import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel, Rating}
import java.io.{BufferedWriter, FileWriter}


import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.util.Random
import au.com.bytecode.opencsv.CSVWriter
import java.text.SimpleDateFormat


object DataProcessing {


  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Simple Application").setMaster("local[*]")
    val sc = new SparkContext(conf)

    //format.parse(fields(3)).getTime(),
    //    To get timestamp from dates given
    val format = new java.text.SimpleDateFormat("yyyy-dd-mm")
    val time = format.parse("2003-01-16").getTime()

    val file = new File("/Users/sonalichaudhari/Desktop/netflix-prize-data/data/combined_3.csv")
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write("movieId,userId,rating,timestamp" + "\n")
    var app = ""
    val filename = "/Users/sonalichaudhari/Desktop/netflix-prize-data/combined_data_3.txt"
    for (line <- Source.fromFile(filename).getLines) {

      if (line.contains(":")) {
        app = line.toString().stripSuffix(":")
      }
      else {
        //           println(app + "," + line)
        var entry = app + "," + line
        bw.write(entry + "\n")
      }
    }
    bw.close()




    // load ratings and movie titles

    val ratings = sc.textFile(new File("/Users/sonalichaudhari/Desktop/DataFinal/Train2.txt").toString).map { line =>
      val fields = line.split(",")
      // format: (timestamp % 10, Rating(userId, movieId, rating))
      (format.parse(fields(3)).getTime(), Rating(fields(1).toInt, fields(0).toInt, fields(2).toDouble))
    }

    //    ratings.collect().foreach(println)
    val movies = sc.textFile(new File("/Users/sonalichaudhari/Desktop/DataFinal/movie_titles.txt").toString).map { line =>
      val fields = line.split(",")
      // format: (movieId, movieName)
      (fields(0).toInt, fields(2))
    }.collect().toMap


    val topmovie = Array("Pirates of the Caribbean: The Curse of the Black Pearl", "Forrest Gump", "Independence Day",
    "Gone in 60 Seconds","Pretty Woman","The Godfather","Miss Congeniality","The Patriot","Twister","The Day After Tomorrow")

    val topmovieid = Array(1,3,45,60,196,32,8,55,32,42)
    val abc = (topmovie zip topmovieid)map {
      case (x, y) => (x, y)
    };
    val RW = new BufferedWriter(new FileWriter("/Users/sonalichaudhari/Desktop/Project/PersonalRatingsLatest.txt"))
    for ( i <- 0 to (topmovie.length - 1)) {
      println(s"${topmovie(i)}: ")
      var success = false
      var rating = 0d

      while(!success) {
        try {
          rating = readDouble()
          success = true
        } catch {
          case exception: Any => {println(s"Wrong input, please rate ${topmovie(i)}:")}
        }
      }
      RW.write("0::"+topmovieid(i)+"::"+rating+"::1400000000"+"\n")
//    println(topmovie(i)+" "+topmovieid(i)+" "+rating)
    }
    RW.close()



//    val myRatings = loadRatings("/Users/sonalichaudhari/Desktop/netflix-prize-data/personal.txt")
    val myRatings = loadRatings("/Users/sonalichaudhari/Desktop/Project/PersonalRatingsLatest1.txt")
    val myRatingsRDD = sc.parallelize(myRatings, 1)

    myRatingsRDD.collect().foreach(println)

    val numPartitions = 4
    val training = ratings.filter(x => x._2.user >= 77)
      .values
      .union(myRatingsRDD)
      .repartition(numPartitions)
      .cache()
    val test = ratings.filter(x => x._2.user < 777).values.cache()

    val numTraining = training.count()
    val numTest = test.count()

    println("Training: " + numTraining + ", test: " + numTest)

    val model = ALS.train(training, 8, 25, 10)
    val predictions = model.predict(test.map(x => (x.user, x.product)))

    predictions.collect().foreach(println)

    val predictionsAndRatings = predictions.map(x => ((x.user, x.product), x.rating))
      .join[Double](test.map(x => ((x.user, x.product), x.rating))).values

    predictionsAndRatings.collect().foreach(println)
    println(math.sqrt(predictionsAndRatings.map(x => (x._1 - x._2) * (x._1 - x._2)).reduce(_ + _) / numTest))

    val recommendations = model.recommendProducts(0, 10)
    var i = 1
    println("Movies recommended for you, based on your previous ratings:")
    recommendations.foreach { recommendation =>
      println(s"#$i: ${movies.get(recommendation.product).get} (expecting a rating of ${recommendation.rating})")
      i += 1
    }


    //    val predictedRates =
    //          model.predict(test.map { case Rating(user,item,rating) => (user,item)} ).map { case Rating(user, product, rate) =>
    //            ((user, product), rate)
    //          }.persist()
    //
    //        val ratesAndPreds = test.map { case Rating(user, product, rate) =>
    //          ((user, product), rate)
    //        }.join(predictedRates)
    //
    //        val MSE = ratesAndPreds.map { case ((user, product), (r1, r2)) => Math.pow((r1 - r2), 2) }.mean()
    //        val rmse = math.sqrt(MSE)


    // clean up
    sc.stop()
  }
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


