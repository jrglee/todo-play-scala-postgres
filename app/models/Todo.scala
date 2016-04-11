package models

import play.api.libs.json.Json

object Todo {
  implicit val todoWriter = Json.writes[Todo]
}

case class Todo(title: String, order: Int, completed: Boolean)
