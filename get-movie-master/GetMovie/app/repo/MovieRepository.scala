package repo

import javax.inject.{Inject, Singleton}

import models.Flight
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

@Singleton()
class MovieRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends MovieTable with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

    //For future use of inserting data directly into the application
  def insert(movie: Flight): Future[Int] = db.run {
    movieTableQueryInc += movie
  }

  def insertAll(movies: List[Flight]): Future[Seq[Int]] = db.run {
    movieTableQueryInc ++= movies
  }

  def update(movie: Flight): Future[Int] = db.run {
    movieTableQuery.filter(_.id === movie.id).update(movie)
  }


  def getAll(): Future[List[Flight]] = db.run {
    movieTableQuery.to[List].result
  }


  def getById(id: Int): Future[Option[Flight]] = db.run {
    movieTableQuery.filter(_.id === id).result.headOption
  }

  def ddl = movieTableQuery.schema

}

private[repo] trait MovieTable {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import driver.api._

  lazy protected val movieTableQuery = TableQuery[MovieTable]
  lazy protected val movieTableQueryInc = movieTableQuery returning movieTableQuery.map(_.id)

  private[MovieTable] class MovieTable(tag: Tag) extends Table[Flight](tag, "flight") {
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
	 val source = column[String]("source")
    val destination = column[String]("destination")
    val carrier = column[String]("carrier")
    val monthOfTravel = column[String]("monthOfTravel")
    val dayOfTravel = column[String]("dayOfTravel")
    val actualPrice = column[String]("actualPrice")
    val predictedPrice = column[String]("predictedPrice")
    



   def * = (source, destination, carrier, monthOfTravel, dayOfTravel, actualPrice, predictedPrice,id.?) <> ((Flight.apply _).tupled, Flight.unapply)
  }


}

