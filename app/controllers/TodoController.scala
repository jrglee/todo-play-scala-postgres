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

  def index = Action.async { implicit request =>
    Future(service.getAllTodos) map { todos =>
      Ok(Json.toJson(todos.map(TodoView.fromModel)))
    }
  }

  def get(id: Long) = Action.async { implicit request =>
    Future(service.getTodo(id)) map {
      case Some(todo) => Ok(Json.toJson(TodoView.fromModel(todo)))
      case None => NotFound
    }
  }

  def add = Action.async(BodyParsers.parse.json) { implicit request =>
    val title = (request.body \ "title").as[String]
    val completed = (request.body \ "completed").asOpt[Boolean].getOrElse(false)
    val order = (request.body \ "order").asOpt[Int].getOrElse(0)

    Future(service.addTodo(title, completed, order)) map {
      case Some(todo) => Ok(Json.toJson(TodoView.fromModel(todo)))
      case None => Ok("")
    }
  }

  def removeAll = Action.async { implicit request =>
    Future(service.removeAllTodos()) map { todo =>
      Ok("")
    }
  }

  def remove(id: Long) = Action.async { implicit request =>
    Future(service.removeTodo(id)) map { _ => Ok("") }
  }

  def update(id: Long) = Action.async(BodyParsers.parse.tolerantJson) { implicit request =>
    val title = (request.body \ "title").asOpt[String]
    val completed = (request.body \ "completed").asOpt[Boolean]
    val order = (request.body \ "order").asOpt[Int]

    Future(service.updateTodo(id, title, completed, order)) map {
      case Some(todo) => Ok(Json.toJson(TodoView.fromModel(todo)))
      case None => Ok("")
    }
  }

}
