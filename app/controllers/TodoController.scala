package controllers

import javax.inject._
import play.api._
import play.api.libs.json.Json
import play.api.mvc._
import anorm._
import anorm.SqlParser.{scalar, str, int, bool}
import play.api.db.{Database, DBApi, DB}

@Singleton
class TodoController @Inject()(db: Database) extends Controller {

  object Todo {
    val dbParser = str("title") ~ int("ord") ~ bool("completed") map {
      case title ~ order ~ completed => Todo(title, order, completed)
    }

    implicit val todoWriter = Json.writes[Todo]
  }

  case class Todo(title: String, order: Int, completed: Boolean)

  def index = Action {
    db.withConnection { implicit conn =>
      val result: List[Todo] = SQL("SELECT * FROM todo ORDER BY ord").as(Todo.dbParser.*)
      Ok(Json.toJson(result))
    }
  }

  def add = Action(BodyParsers.parse.json) { request =>
    db.withConnection { implicit conn =>
      val result: Option[Long] = SQL("INSERT INTO todo(id,title,completed,ord) values(default,{title},{completed},{order})")
        .on(
          "title" -> (request.body \ "title").as[String],
          "completed" -> (request.body \ "completed").asOpt[Boolean].getOrElse(false),
          "order" -> (request.body \ "order").asOpt[Int].getOrElse(0))
        .executeInsert()

      result.map(id => SQL("SELECT * FROM todo WHERE id = {id}").on("id" -> id).as(Todo.dbParser.single))
        .map(todo => Ok(Json.toJson(todo)))
        .getOrElse(Ok(""))
    }
  }

  def remove = Action { request =>
    db.withConnection { implicit conn =>
      SQL("DELETE FROM todo").execute()
      Ok("")
    }
  }
}
