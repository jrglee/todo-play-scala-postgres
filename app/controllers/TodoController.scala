package controllers

import javax.inject._
import play.api._
import play.api.libs.json.Json
import play.api.mvc._

@Singleton
class TodoController @Inject() extends Controller {
  def index = Action {
    Ok(Json.toJson(List[String]()))
  }

  def add = Action(BodyParsers.parse.json) { request =>
    Ok(request.body)
  }

  def remove = Action { request =>
    Ok("")
  }
}
