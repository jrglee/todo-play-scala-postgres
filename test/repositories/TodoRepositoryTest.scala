package repositories

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{MustMatchers, WordSpec}
import play.api.db.evolutions.Evolutions
import play.api.db.{Database, Databases}

class TodoRepositoryTest extends WordSpec
  with ScalaFutures
  with MustMatchers {

  def withMyDatabase[T](block: Database => T) = {
    Databases.withInMemory(
      name = "default",
      urlOptions = Map(
        "MODE" -> "PostgreSQL"
      ),
      config = Map(
        "logStatements" -> true
      )
    )(block)
  }

  def withRepo[T](block: TodoRepository => T) = withMyDatabase { db =>
    Evolutions.applyEvolutions(db)
    block(new TodoRepository(db))
  }

  "TodoRepository#getAllTodos" when {
    "has no TODOs" should {
      "return empty" in withRepo { repo =>
        whenReady(repo.getAllTodos, timeout(Span(6, Seconds))) { result =>
          result mustBe empty
        }
      }
    }
  }
}
