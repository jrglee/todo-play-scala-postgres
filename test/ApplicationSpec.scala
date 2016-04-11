import org.scalatest.TestData
import org.scalatestplus.play._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.test._

class ApplicationSpec extends PlaySpec with OneAppPerTest {

  override def newAppForTest(td: TestData) = new GuiceApplicationBuilder()
    .configure(inMemoryDatabase())
    .build()

  "Routes" should {

    "send 404 on a bad request" in {
      route(app, FakeRequest(GET, "/boum")).map(status) mustBe Some(NOT_FOUND)
    }
  }

  "TodoController" should {

    "return empty content" in {
      val home = route(app, FakeRequest(GET, "/todo")).get

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      contentAsJson(home).as[List[String]] mustBe empty
    }
  }
}
