package controllers

import models.Todo
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.mvc.Results
import play.api.test.Helpers._
import play.api.test._
import services.TodoService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TodoControllerTest extends PlaySpec
  with Results
  with MockitoSugar {

  trait Subject {
    val service = mock[TodoService]
    val controller = new TodoController(service)
  }

  "TodoController#index" should {
    "list all TODOs" in new Subject {
      when(service.getAllTodos).thenReturn(Future(List(Todo(1L, "My Todo", 10, completed = false))))

      val result = controller.index.apply(FakeRequest("GET", "http://localhost/todo"))

      status(result) mustEqual OK

      (contentAsJson(result) \ 0 \ "title").as[String] mustEqual "My Todo"
      (contentAsJson(result) \ 0 \ "order").as[Int] mustEqual 10
      (contentAsJson(result) \ 0 \ "completed").as[Boolean] mustBe false
      (contentAsJson(result) \ 0 \ "url").as[String] mustEqual "http://localhost/todo/1"
    }
  }

  "TodoController#get" should {
    "get existing TODO" in new Subject {
      when(service.getTodo(10)).thenReturn(Future(Some(Todo(1L, "My Todo", 10, completed = false))))

      val result = controller.get(10).apply(FakeRequest("GET", "http://localhost/todo/10"))

      status(result) mustEqual OK

      (contentAsJson(result) \ "title").as[String] mustEqual "My Todo"
      (contentAsJson(result) \ "order").as[Int] mustEqual 10
      (contentAsJson(result) \ "completed").as[Boolean] mustBe false
      (contentAsJson(result) \ "url").as[String] mustEqual "http://localhost/todo/1"
    }

    "ignore inexisting TODOs" in new Subject {
      when(service.getTodo(10)).thenReturn(Future(None))

      val result = controller.get(10).apply(FakeRequest("GET", "http://localhost/todo/10"))

      status(result) mustEqual NOT_FOUND
    }
  }
}
