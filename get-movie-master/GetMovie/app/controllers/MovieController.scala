package controllers

import com.google.inject.Inject
import models.Flight
import play.api.Logger
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json._
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc._
import repo.MovieRepository
import utils.Constants
import utils.JsonFormat._

import scala.concurrent.Future

/**
  * Handles all requests related to Flight
  */
class MovieController @Inject()(movieRepository: MovieRepository, val messagesApi: MessagesApi) extends Controller with I18nSupport {

  import Constants._

  val logger = Logger(this.getClass())

  /**
    * Handles request for getting all flights from the database
    */
  def list() = Action.async {
    movieRepository.getAll().map { res =>
      logger.info("Movie list: " + res)
      Ok(successResponse(Json.toJson(res), Messages("movie.success.movieList")))
    }
  }




  private def errorResponse(data: JsValue, message: String) = {
    obj("status" -> ERROR, "data" -> data, "msg" -> message)
  }



  def search(movieId: Int) = Action.async(parse.json) {request =>
	logger.info("Movie Json ===> " + request.body)
	request.body.validate[Flight].fold(
	error => Future.successful(BadRequest(JsError.toJson(error))),
	{
	movie =>
		movieRepository.getById(movieId).map { res => Ok(successResponse(Json.toJson("{}"), Messages("movie.success.selected"))) }
	})
  }
 
  
  
 

  private def successResponse(data: JsValue, message: String) = {
    obj("status" -> SUCCESS, "data" -> data, "msg" -> message)
  }

}
