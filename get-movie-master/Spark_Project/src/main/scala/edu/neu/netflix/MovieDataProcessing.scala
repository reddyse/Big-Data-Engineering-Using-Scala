package edu.neu.netflix

import java.io.File

import scala.io.Source
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd._
import org.apache.spark.mllib.recommendation.{ALS, MatrixFactorizationModel, Rating}
import java.io.{BufferedWriter, FileWriter}


/**
  * Created by Sonali/Praneeth on 16/4/2016.
  * An example of regression neural network for performing addition
  */
object MovieDataProcessing {

  def main(args: Array[String]) = {


    val conf = new SparkConf().setAppName("Simple Application").setMaster("local[*]")
    val sc = new SparkContext(conf)

    // To get timestamp from dates given
    val format = new java.text.SimpleDateFormat("yyyy-dd-mm")
    val time = format.parse("2003-01-16").getTime()

    // Generating a combined file in the required format
    val NtflixRecosFile = "/Users/praneethreddy/Downloads/"
    val file = new File("/Users/praneethreddy/Downloads/Train.txt")
    val bw = new BufferedWriter(new FileWriter(file))
    var train_files = Array("combined_data_1.txt", "combined_data_2.txt", "combined_data_3.txt", "combined_data_4.txt")
    for (i <- 0 to (train_files.length - 1)) {
      var app = ""
      for (line <- Source.fromFile(NtflixRecosFile + train_files(i)).getLines) {
        if (line.contains(":")) {
          app = line.toString().stripSuffix(":")
        }
        else {
          var entry = app + "," + line
          bw.write(entry + "\n")
        }
      }
    }
    println(("Combined file generated!!"))
    bw.close()

    // Load ratings and movie titles
    val ratings = sc.textFile(new File("/Users/praneethreddy/Downloads/Train2.txt").toString).map { line =>
      val fields = line.split(",")
      // format: (timestamp % 10, Rating(userId, movieId, rating))
      (format.parse(fields(3)).getTime(), Rating(fields(1).toInt, fields(0).toInt, fields(2).toDouble))
    }

    // Load ratings and movie_titles file
    val movies = sc.textFile(new File("/Users/praneethreddy/Downloads/movie_titles.txt").toString).map { line =>
      val fields = line.split(",")
      // format: (movieId, movieName)
      (fields(0).toInt, fields(2))
    }.collect().toMap

    println("PLEASE RATE FOLLOWING MOVIES BETWEEN 1(Low) to 5(High)")
    println("<----------------------------------------------->")
    // Storing the top rated movie in the data considered and their respective ids
    val topmovie = Array("Ray", "Speed", "Reservoir Dogs", "Mean Girls", "Something's Gotta Give", "X2: X-Men UnitedKill Bill: Vol. 2", "American Beauty", "Rush Hour", "Pay It Forward")
    val topmovieid = Array(886, 607, 175, 758, 30, 191, 457, 571, 483, 313)

    // Taking ratings from a particular user and storing it in a file
    val RW = new BufferedWriter(new FileWriter("/Users/praneethreddy/Downloads/PersonalRatingsLatest.txt"))
    for (i <- 0 to (topmovie.length - 1)) {
      println(s"${topmovie(i)}: ")
      var success = false
      var rating = 0d

      while (!success) {
        try {
          rating = readDouble()
          success = true
        } catch {
          case exception: Any => {
            println(s"Wrong input, please rate ${topmovie(i)}:")
          }
        }
      }
      RW.write("0," + topmovieid(i) + "," + rating + ",1400000000" + "\n")
    }
    RW.close()

    // Loading the personal ratings by a user file in a val
    val myRatings = load("/Users/praneethreddy/Downloads/PersonalRatingsLatest.txt")
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
    val model = ALS.train(training, 8, 5, 0.09)

    // Implementing trained model on the test data
    val prediction = model.predict(test.map(x => (x.user, x.product)))

    // Joining predicted values and actual values of the test data
    val predRatings = prediction.map(x => ((x.user, x.product), x.rating))
      .join[Double](test.map(x => ((x.user, x.product), x.rating))).values

    // Calculating the RMSE value for the model
    val MSE = math.sqrt((predRatings.map(x => (x._1 - x._2) * (x._1 - x._2)).reduce(_+_) / numTest))
    print("MSE value is --->"+MSE)

    // Taking the top movies and recommending to the user
    val recommendations = model.recommendProducts(0, 10)
    var i = 1
    println("MOVIES RECOMMENDED FOR YOU")
    println("<------------------------->")
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








    //---------------------




    /*//Setup
    import LocalSparkContext._*/

    //-------------

  /*
  //Random number generator seed, for reproducability
  val seed = 12345
  //Number of iterations per minibatch
  val iterations = 1
  //Number of epochs (full passes of the data)
  val nEpochs = 1000
  //Number of data points
  val nSamples = 1000
  //Batch size: i.e., each epoch has nSamples/batchSize parameter updates
  val batchSize = 1000
  //Network learning rate
  val learningRate = 0.01
  // The range of the sample data, data in range (0-1 is sensitive for NN, you can try other ranges and see how it effects the results
  // also try changing the range along with changing the activation function
  val ntrain = 35000
  val MIN_RANGE = 0
  val MAX_RANGE = 3

  val rng = new Random(seed)

  def main(args: Array[String]): Unit = {
    //Generate the training data
    val iterator = getTrainingData(batchSize, rng)
    val numInput = 7
    val numOutputs = 1
    val nHidden = 4
    val net: MultiLayerNetwork = new MultiLayerNetwork(new NeuralNetConfiguration.Builder()
      .seed(seed)
      .iterations(iterations)
      .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
      .learningRate(learningRate)
      .weightInit(WeightInit.XAVIER)
      .updater(Updater.NESTEROVS).momentum(0.9)
      .list()
      .layer(0, new DenseLayer.Builder().nIn(numInput).nOut(nHidden)
        .activation("tanh")
        .build())
      .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
        .activation("identity")
        .nIn(nHidden).nOut(numOutputs).build())
      .pretrain(false).backprop(true).build()
    )
    net.init()
    net.setListeners(new ScoreIterationListener(1))

    //Train the network on the full data set, and evaluate in periodically
    (0 until nEpochs).foreach { _ =>
      iterator.reset()
      print(net.fit(iterator))
      //print()
    }
    //Create the network and Test
    testData(net,iterator)
  }

  /**
    * Test data against the Trained Dataset
    * @param net
    * @param iterator
    */
  def testData(net:MultiLayerNetwork,iterator : DataSetIterator): Unit ={

    //Testing Phase
    //Read CSV
    val bufferedSource = scala.io.Source.fromFile("test.csv")

    val z:Array[Double] = new Array[Double](ntrain)

    var i=0
    for (line <- bufferedSource.getLines.drop(1)) {
      val cols = line.split(",").map(_.trim)
      val input = Nd4j.create(Array[Double](cols(1).toDouble,cols(2).toDouble,cols(3).toDouble,cols(4).toDouble,cols(5).toDouble,cols(6).toDouble,cols(7).toDouble), Array[Int](1, 7))
      val out = net.output(input, false)
      val outdata = out.getDouble(0,0)
      System.out.println("|" + cols(0).toDouble + "|" +outdata + "|")
      // estimate accuracy
      z(i) = mse(cols(0).toDouble,outdata)
      i=i+1;
      //Insert to Db
      //val params = Array(cols(1), "New York","CA", "10/11/2016","10/11/2016", "1234","256.8","70.23343", "67.2323232323")
      val params = Array(cols(11), cols(12),cols(10), cols(8),cols(9),  cols(0).toString,outdata.toString,"70.23343", "67.2323232323",cols(17).toString,cols(18).toString,cols(19).toString,cols(20).toString)
      DAO.insertToDb(params)
    }
    bufferedSource.close
    println("RMS Error : " + rms(z))

  }
  /**
    * Returns Root Mean Square
    * @param nums
    * @return
    */
  def rms(nums: Seq[Double]) = math.sqrt( ((nums).sum / nums.size))

  /**
    * Returns Mean Square Error
    * @param num1
    * @param num2
    * @return
    */
  def mse(num1: Double,num2:Double):Double = math.pow((num2 - num1),2)

  /**
    * Generate Training Data
    * @param batchSize
    * @param rand
    * @return
    */
  def getTrainingData(batchSize: Int, rand: Random): DataSetIterator = {

    //Input Parameters
    val carrerinput1Builder = Array.newBuilder[Double]
    val destinationinput2Builder = Array.newBuilder[Double]
    val sourceinput3Builder = Array.newBuilder[Double]
    val baseFareinput4Builder = Array.newBuilder[Double]
    val monthsInput5Builder = Array.newBuilder[Double]
    val daysInput6Builder = Array.newBuilder[Double]
    val seatInput7Builder = Array.newBuilder[Double]

    //Output Label
    val outputTicketFareBuilder = Array.newBuilder[Double]

    //Read CSV
    val bufferedSource = scala.io.Source.fromFile("train_nn.csv")
    for (line <- bufferedSource.getLines.drop(1)) {
      val cols = line.split(",").map(_.trim)
      // do whatever you want with the columns here
      carrerinput1Builder += cols(1).toDouble
      destinationinput2Builder += cols(2).toDouble
      sourceinput3Builder += cols(3).toDouble
      baseFareinput4Builder += cols(4).toDouble
      monthsInput5Builder += cols(5).toDouble //5 for Normalized data
      daysInput6Builder += cols(6).toDouble //6 for Normalized data
      seatInput7Builder += cols(7).toDouble
      outputTicketFareBuilder  += cols(0).toDouble
    }
    bufferedSource.close

    val inputNDArray1 = Nd4j.create(carrerinput1Builder.result(), Array[Int](nSamples, 1))
    val inputNDArray2 = Nd4j.create(destinationinput2Builder.result(), Array[Int](nSamples, 1))
    val inputNDArray3 = Nd4j.create(sourceinput3Builder.result(), Array[Int](nSamples, 1))
    val inputNDArray4 = Nd4j.create(baseFareinput4Builder.result(), Array[Int](nSamples, 1))
    val inputNDArray5 = Nd4j.create(monthsInput5Builder.result(), Array[Int](nSamples, 1))
    val inputNDArray6 = Nd4j.create(daysInput6Builder.result(), Array[Int](nSamples, 1))
    val inputNDArray7 = Nd4j.create(seatInput7Builder.result(), Array[Int](nSamples, 1))

    val outputNDArray = Nd4j.create(outputTicketFareBuilder.result(), Array[Int](nSamples, 1))

    val inputNDArray = Nd4j.hstack(inputNDArray1, inputNDArray2, inputNDArray3,inputNDArray4,inputNDArray5,inputNDArray6,inputNDArray7)
    val allData: DataSet = new DataSet(inputNDArray, outputNDArray)
    val list: List[DataSet] = allData.asList()
    Collections.shuffle(list)
    new ListDataSetIterator(list, batchSize)

  }*/


