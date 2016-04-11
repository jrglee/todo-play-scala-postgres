package controllers

import javax.inject._

import play.api.libs.json.Json
import play.api.mvc._
import services.TodoService

@Singleton
class TodoController @Inject()(service: TodoService) extends Controller {

  def index = Action {
    Ok(Json.toJson(service.getAllTodos))
  }

  def add = Action(BodyParsers.parse.json) { request =>
    val title = (request.body \ "title").as[String]
    val completed = (request.body \ "completed").asOpt[Boolean].getOrElse(false)
    val order = (request.body \ "order").asOpt[Int].getOrElse(0)

    service.addTodo(title, completed, order)
      .map(todo => Ok(Json.toJson(todo)))
      .getOrElse(Ok(""))
  }

  def remove = Action { request =>
    service.removeAllTodos()
    Ok("")
  }
}
