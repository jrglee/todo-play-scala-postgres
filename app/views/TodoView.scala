package views

import models.Todo
import play.api.libs.json.Json

object TodoView {
  implicit val todoWriter = Json.writes[TodoView]

  def apply(todo: Todo): TodoView = new TodoView(
    title = todo.title,
    order = todo.order,
    completed = todo.completed,
    url = s"/${todo.id.toString}"
  )
}

case class TodoView(title: String, order: Int, completed: Boolean, url: String)