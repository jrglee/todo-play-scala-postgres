package controllers

import javax.inject._

import play.api.libs.json.Json
import play.api.mvc._
import repositories.TodoRepository
import views.TodoView

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class TodoController @Inject()(repository: TodoRepository) extends Controller {

  def index = Action.async { implicit request =>
    repository.getAllTodos map { todos =>
      Ok(Json.toJson(todos.map(TodoView.fromModel)))
    }
  }

  def get(id: Long) = Action.async { implicit request =>
    repository.getTodo(id) map {
      case Some(todo) => Ok(Json.toJson(TodoView.fromModel(todo)))
      case None => NotFound
    }
  }

  def add = Action.async(BodyParsers.parse.json) { implicit request =>
    val title = (request.body \ "title").as[String]
    val completed = (request.body \ "completed").asOpt[Boolean].getOrElse(false)
    val order = (request.body \ "order").asOpt[Int].getOrElse(0)

    repository.addTodo(title, completed, order) map {
      case Some(todo) => Ok(Json.toJson(TodoView.fromModel(todo)))
      case None => InternalServerError
    }
  }

  def removeAll() = Action.async {
    repository.removeAllTodos() map { _ => Ok("") }
  }

  def remove(id: Long) = Action.async {
    repository.removeTodo(id) map { _ => Ok("") }
  }

  def update(id: Long) = Action.async(BodyParsers.parse.tolerantJson) { implicit request =>
    val title = (request.body \ "title").asOpt[String]
    val completed = (request.body \ "completed").asOpt[Boolean]
    val order = (request.body \ "order").asOpt[Int]

    repository.updateTodo(id, title, completed, order) map {
      case Some(todo) => Ok(Json.toJson(TodoView.fromModel(todo)))
      case None => NotFound
    }
  }

}
