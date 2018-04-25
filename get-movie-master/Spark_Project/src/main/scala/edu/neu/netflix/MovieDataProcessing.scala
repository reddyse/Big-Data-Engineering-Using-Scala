
import java.io.File

import scala.io.Source
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.mllib.recommendation.{ALS,Rating}
import java.io.{BufferedWriter, FileWriter}
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel


object DataProcessing {


  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Simple Application").setMaster("local[*]")
    val sc = new SparkContext(conf)

    // To get timestamp from dates given
    val format = new java.text.SimpleDateFormat("yyyy-dd-mm")
    val time = format.parse("2003-01-16").getTime()

    // Generating a combined file in the required format
    val NtflixRecosFile = "/Users/sonalichaudhari/Desktop/netflix-prize-data/"
    val file = new File("/Users/sonalichaudhari/Desktop/netflix-prize-data/Train.txt")
    val bw = new BufferedWriter(new FileWriter(file))
    var train_files = Array("combined_data_1.txt","combined_data_2.txt","combined_data_3.txt","combined_data_4.txt")
        for ( i <- 0 to (train_files.length - 1)) {
          var app = ""
          for (line <- Source.fromFile(NtflixRecosFile+train_files(i)).getLines) {
            if (line.contains(":")) {
              app = line.toString().stripSuffix(":")
            }
            else {
              var entry = app+","+line
              bw.write(entry+"\n")
            }
          }
        }
    println(("Combined file generated!!"))
    bw.close()

    // Load ratings and movie titles
    val ratings = sc.textFile(new File("/Users/sonalichaudhari/Desktop/DataFinal/Train.txt").toString).map { line =>
      val fields = line.split(",")
      // format: (timestamp % 10, Rating(userId, movieId, rating))
      (format.parse(fields(3)).getTime(), Rating(fields(1).toInt, fields(0).toInt, fields(2).toDouble))
    }

    // Load ratings and movie_titles file
    val movies = sc.textFile(new File("/Users/sonalichaudhari/Desktop/DataFinal/movie_titles.txt").toString).map { line =>
      val fields = line.split(",")
      // format: (movieId, movieName)
      (fields(0).toInt, fields(2))
    }.collect().toMap


    // Storing the top rated movie in the data considered and their respective ids
    val topmovie = Array("Ray","Speed","Reservoir Dogs","Mean Girls","Something's Gotta Give","X2: X-Men UnitedKill Bill: Vol. 2","American Beauty","Rush Hour","Pay It Forward")
    val topmovieid = Array(886,607,175,758,30,191,457,571,483,313)

    // Taking ratings from a particular user and storing it in a file
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
      RW.write("0,"+topmovieid(i)+","+rating+",1400000000"+"\n")
    }
    RW.close()

    // Loading the personal ratings by a user file in a val
    val myRatings = load("/Users/sonalichaudhari/Desktop/Project/PersonalRatingsLatest.txt")
    val myRatingsRDD = sc.parallelize(myRatings, 1)

    // MACHINE LERANING
    //Splitting data into train(80%) and test(20%)
    val proportion = 0.80
    val seed = 42
    val split = ratings.randomSplit(Array(proportion, 1 - proportion), seed)
    val train_set = split(0).cache()
    val test = split(1).values.cache()

    val training = train_set.values.union(myRatingsRDD).cache()
    val numTraining = training.count()
    val numTest = test.count()

    println("Training: " + numTraining + ", test: " + numTest)

    // Model training
    val model = ALS.train(training,8,10,0.01)

    // Implementing trained model on the test data
    val prediction = model.predict(test.map(x => (x.user, x.product)))

    // Joining predicted values and actual values of the test data
    val predRatings = prediction.map(x => ((x.user, x.product), x.rating))
      .join[Double](test.map(x => ((x.user, x.product), x.rating))).values

    // Calculating the RMSE value for the model
    println("RMSE: "+math.sqrt(predRatings.map(x => (x._1 - x._2) * (x._1 - x._2)).reduce(_ + _) / numTest))
    

    // Taking the top movies and recommending to the user
    val recommendations = model.recommendProducts(0, 10)
    var i = 1
    println("Movies recommended for you, based on your previous ratings:")
    recommendations.foreach { recommendation =>
      println(s"#$i: ${movies.get(recommendation.product).get} (expecting a rating of ${recommendation.rating})")
      i += 1
    }

    sc.stop()
  }

  // Loading the file
  def load(path: String): Seq[Rating] = {
    val lines = Source.fromFile(path).getLines()
    val ratings = lines.map { line =>
      val fields = line.split(",")
      Rating(fields(0).toInt, fields(1).toInt, fields(2).toDouble)
    }.filter(_.rating > 0.0)
    if (ratings.isEmpty) {
      sys.error("No ratings provided.")
    } else {
      ratings.toSeq
    }
  }

}


