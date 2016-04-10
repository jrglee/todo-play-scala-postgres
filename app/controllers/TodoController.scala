package controllers

import javax.inject._
import play.api._
import play.api.mvc._

@Singleton
class TodoController @Inject() extends Controller {
  def index = Action {
    Ok("")
  }

  def add = Action { request =>
    Ok("")
  }

}
