package controllers

import javax.inject._

import play.api.libs.json.Json
import play.api.mvc._
import services.TodoService
import views.TodoView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class TodoController @Inject()(service: TodoService) extends Controller {

  def index = Action.async {
    Future(service.getAllTodos) map { todos =>
      Ok(Json.toJson(todos.map(TodoView.apply)))
    }
  }

  def add = Action.async(BodyParsers.parse.json) { request =>
    val title = (request.body \ "title").as[String]
    val completed = (request.body \ "completed").asOpt[Boolean].getOrElse(false)
    val order = (request.body \ "order").asOpt[Int].getOrElse(0)

    Future(service.addTodo(title, completed, order)) map {
      case Some(todo) => Ok(Json.toJson(TodoView(todo)))
      case None => Ok("")
    }
  }

  def remove = Action.async { request =>
    Future(service.removeAllTodos()) map { todo =>
      Ok("")
    }
  }
}
