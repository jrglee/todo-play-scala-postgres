package controllers

import javax.inject._
import play.api._
import play.api.libs.json.Json
import play.api.mvc._
import anorm._
import anorm.SqlParser.{scalar, str, int}
import play.api.db.{Database, DBApi, DB}

@Singleton
class TodoController @Inject()(db: Database) extends Controller {

  object Todo {
    val dbParser = str("title") ~ int("ord") map {
      case title ~ order => Todo(title, order)
    }

    implicit val todoWriter = Json.writes[Todo]
  }

  case class Todo(title: String, order: Int)

  def index = Action {
    db.withConnection { implicit conn =>
      val result: List[Todo] = SQL("SELECT * FROM todo ORDER BY ord").as(Todo.dbParser.*)
      Ok(Json.toJson(result))
    }
  }

  def add = Action(BodyParsers.parse.json) { request =>
    db.withConnection { implicit conn =>
      SQL("INSERT INTO todo(id,title,completed,ord) values(default,{title},false,{order})")
        .on(
          "title" -> (request.body \ "title").as[String],
          "order" -> (request.body \ "order").asOpt[Int].getOrElse(0))
        .executeInsert()
      Ok(request.body)
    }
  }

  def remove = Action { request =>
    db.withConnection { implicit conn =>
      SQL("DELETE FROM todo").execute()
      Ok("")
    }
  }
}
