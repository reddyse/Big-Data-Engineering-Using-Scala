
import org.specs2.mock.Mockito
import play.api.test._
import play.api.test.Helpers._
import play.api.Environment
import play.api.i18n.{DefaultLangs, DefaultMessagesApi}
import play.api.libs.json.Json
import play.api.mvc._
import play.api.test._
import utils.JsonFormat._
import play.api.Application
import repo.MovieRepository
import play.api.test.{WithApplication, PlaySpecification}

import scala.concurrent.Future
/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends PlaySpecification with Mockito with Results {

  "Application" should {
          
def movieRepository(implicit app: Application) = Application.instanceCache[MovieRepository].apply(app)


    "send 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET, "/boum")) must beSome.which (status(_) == NOT_FOUND)
    }
    
      "get all rows" in new WithApplication()  {
      val result = await(movieRepository.getAll)
      //result.length === 2254
      result.head.source === "Atlanta GA"
    }
  }
}