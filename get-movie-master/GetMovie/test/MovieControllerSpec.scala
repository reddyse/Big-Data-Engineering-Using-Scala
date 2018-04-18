package controllers


import models.Flight
import org.specs2.mock.Mockito
import play.api.Environment
import play.api.i18n.{DefaultLangs, DefaultMessagesApi}
import play.api.libs.json.Json
import play.api.mvc._
import play.api.test._
import repo.MovieRepository
import utils.JsonFormat._
import play.api.test.{WithApplication, PlaySpecification}

import scala.concurrent.Future

class MovieControllerSpec extends PlaySpecification with Mockito with Results {

  implicit val mockedRepo: MovieRepository = mock[MovieRepository]


  "MovieController" should {
      
    "get all list" in new WithFltApplication() {
      val flight = Flight("B", "N","CA", "12","11", "1234","1234", Some(1))
      mockedRepo.getAll() returns Future.successful(List(flight))
      val result = flightController.list().apply(FakeRequest())
      val resultAsString = contentAsString(result)
      resultAsString === """{"status":"success","data":[{"source":"B","destination":"N","carrier":"CA","monthOfTravel":"12","dayOfTravel":"11","actualPrice":"1234","predictedPrice":"1234","id":1}],"msg":"Getting Flight list successfully."}"""
    }
    
   

  }

}

class WithFltApplication(implicit mockedRepo: MovieRepository) extends WithApplication {
  val messageAPI = new DefaultMessagesApi(Environment.simple(), app.configuration, new DefaultLangs(app.configuration))
  val flightController = new MovieController(mockedRepo, messageAPI)
}

