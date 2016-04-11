package models

import play.api.libs.json.Json

object Todo {
  implicit val todoWriter = Json.writes[Todo]
}

case class Todo(id: Long, title: String, order: Int, completed: Boolean)
