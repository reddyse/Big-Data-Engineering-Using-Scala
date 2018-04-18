package bootstrap

import com.google.inject.Inject
import javax.inject.Singleton
import repo.MovieRepository
import models.Flight
import java.util.Date
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.Logger
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class InitialData @Inject() (movieRepo: MovieRepository) {

  def insert = for {
    movies <- movieRepo.getAll() if (movies.length == 0)
    _ <- movieRepo.insertAll(Data.movies)
  } yield {}

  try {
    Logger.info("DB initialization.................")
    Await.result(insert, Duration.Inf)
  } catch {
    case ex: Exception =>
      Logger.error("Error in database initialization ", ex)
  }

}

object Data {
  val movies = List(
    Flight("B", "N","CA", "12","11", "1234","1234"),
    Flight("N", "B","CA", "11","15", "1234","1234"),
	Flight("F", "York","CA", "10","11", "1234","1234"),
    Flight("GH", "RH","CA", "10","18", "1234","1234"))
}

