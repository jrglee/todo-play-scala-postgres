package views

import models.Todo
import play.api.libs.json.Json
import play.api.mvc.Request

object TodoView {
  implicit val todoWriter = Json.writes[TodoView]

  def apply(todo: Todo)(implicit request: Request[_]): TodoView = new TodoView(
    title = todo.title,
    order = todo.order,
    completed = todo.completed,
    url = s"${if (request.secure) "https" else "http"}://${request.host}/todo/${todo.id.toString}"
  )
}

case class TodoView(title: String, order: Int, completed: Boolean, url: String)