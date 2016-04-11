package controllers

import javax.inject._

import play.api.libs.json.Json
import play.api.mvc._
import services.TodoService

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class TodoController @Inject()(service: TodoService) extends Controller {

  def index = Action.async {
    Future(service.getAllTodos) map { todos =>
      Ok(Json.toJson(todos))
    }
  }

  def add = Action.async(BodyParsers.parse.json) { request =>
    val title = (request.body \ "title").as[String]
    val completed = (request.body \ "completed").asOpt[Boolean].getOrElse(false)
    val order = (request.body \ "order").asOpt[Int].getOrElse(0)

    Future(service.addTodo(title, completed, order)) map { todo =>
      Ok(Json.toJson(todo))
    }
  }

  def remove = Action.async { request =>
    Future(service.removeAllTodos()) map { todo =>
      Ok("")
    }
  }
}
