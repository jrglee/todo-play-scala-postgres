package services

import javax.inject.{Inject, Singleton}

import anorm.SqlParser._
import anorm._
import models.Todo
import play.api.db.Database

@Singleton
class TodoService @Inject()(db: Database) {

  val dbParser = long("id") ~ str("title") ~ int("ord") ~ bool("completed") map {
    case id ~ title ~ order ~ completed => Todo(id, title, order, completed)
  }

  def getAllTodos = db.withConnection { implicit conn =>
    SQL("SELECT * FROM todo ORDER BY ord").as(dbParser.*)
  }

  def addTodo(title: String, completed: Boolean, order: Int) = db.withConnection { implicit conn =>
    val result: Option[Long] = SQL("INSERT INTO todo(id,title,completed,ord) values(default,{title},{completed},{order})")
      .on("title" -> title, "completed" -> completed, "order" -> order)
      .executeInsert()

    result.map(id => SQL("SELECT * FROM todo WHERE id = {id}")
      .on("id" -> id)
      .as(dbParser.single))
  }

  def removeAllTodos() {
    db.withConnection { implicit conn =>
      SQL("DELETE FROM todo").execute()
    }
  }
}
