package services

import java.sql.Connection
import javax.inject.{Inject, Singleton}

import anorm.SqlParser._
import anorm._
import models.Todo
import play.api.db.Database

@Singleton
class TodoService @Inject()(db: Database) {
  val todoParser = long("id") ~ str("title") ~ int("ord") ~ bool("completed") map {
    case id ~ title ~ order ~ completed => Todo(id, title, order, completed)
  }

  def getAllTodos = db.withConnection { implicit conn =>
    SQL("SELECT * FROM todo ORDER BY ord").as(todoParser.*)
  }

  def getTodo(id: Long) = db.withConnection { implicit conn => getSingleTodo(id) }

  def addTodo(title: String, completed: Boolean, order: Int) = db.withConnection { implicit conn =>
    val result: Option[Long] = SQL("INSERT INTO todo(id,title,completed,ord) values(default,{title},{completed},{order})")
      .on("title" -> title, "completed" -> completed, "order" -> order)
      .executeInsert()

    result.flatMap(getSingleTodo)
  }

  def updateTodo(id: Long,
                 title: Option[String] = None,
                 completed: Option[Boolean] = None) = db.withConnection { implicit conn =>
    getSingleTodo(id).map { todo =>
      SQL("UPDATE todo SET title = {title}, completed = {completed} WHERE id = {id}")
        .on("id" -> id, "title" -> title.getOrElse(todo.title), "completed" -> completed.getOrElse(todo.completed))
        .executeUpdate()
    } match {
      case Some(x: Int) if x > 0 => getSingleTodo(id)
      case _ => None
    }
  }

  def removeAllTodos() {
    db.withConnection { implicit conn =>
      SQL("DELETE FROM todo").execute()
    }
  }

  def removeTodo(id: Long) = db.withConnection { implicit conn =>
    SQL("DELETE FROM todo WHERE id = {id}").on("id" -> id).executeUpdate()
  }

  private def getSingleTodo(id: Long)(implicit connection: Connection) =
    SQL("SELECT * FROM todo WHERE id = {id}")
      .on("id" -> id)
      .as(todoParser.singleOpt)
}
