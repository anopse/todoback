package com.test.todoback.impl.taskcollection

import com.test.todoback.model.*
import zio.{ZIO, IO, Task as ZTask}
import com.test.todoback.model.TaskModificationError
import eu.timepit.refined.types.string.NonEmptyString
import com.test.todoback.config.MongoConfig
import reactivemongo.api.AsyncDriver
import reactivemongo.api.MongoConnection
import reactivemongo.api.bson.collection.BSONCollection
import scala.concurrent.ExecutionContext
import reactivemongo.api.bson._
import com.test.todoback.impl.taskcollection.mongo.TaskBsonCodec.given
import reactivemongo.api.commands.WriteResult

final class MongoTaskCollection(using config: MongoConfig) extends TaskCollection {
  private val connection: ZTask[MongoConnection] = {
    val connectionUri = s"mongodb://${config.username}:${config.password}@${config.host}/${config.authDatabase}"
    val driver = AsyncDriver()

    ZIO.fromFuture(ec => {
      given ExecutionContext = ec

      for {
        parsedUri <- MongoConnection.fromString(connectionUri)
        connection <- driver.connect(parsedUri)
      } yield connection
    })
  }

  private def usingCollection[E, R](f: BSONCollection => IO[E, R]): IO[E | Throwable, R] = {
    connection.flatMap { connection =>
      ZIO.fromFuture(ec => {
        given ExecutionContext = ec
        val db = connection.database(config.database)
        val collection = db.map(_.collection("tasks"))
        collection
      }).flatMap(f)
    }
  }

  override def all(): IO[Throwable, List[Task]] = usingCollection { collection =>
    ZIO.fromFuture(ec =>
      given ExecutionContext = ec
      collection.find(BSONDocument.empty).cursor[Task]().collect[List]()
    )
  }

  override def create(
      name: NonEmptyString,
      description: Option[NonEmptyString],
      status: TaskStatus
  ): IO[Throwable, Task] = usingCollection { collection =>
    val id = java.util.UUID.randomUUID().toString
    val task = Task(TaskId(id), name, description, status)
    ZIO.fromFuture(ec =>
      given ExecutionContext = ec
      collection.insert.one(task)
    ).flatMap {
        case WriteResult.Exception(e) => ZIO.fail(e)
        case _                        => ZIO.succeed(task)
    }
  }

  override def get(taskId: TaskId): IO[TaskModificationError | Throwable, Task] =
    usingCollection { collection =>
        ZIO.fromFuture(ec =>
          given ExecutionContext = ec
          collection.find(BSONDocument("_id" -> taskId.value)).one[Task]
        ).flatMap {
          case Some(task) => ZIO.succeed(task)
          case None       => ZIO.fail(TaskModificationError.NotFoundError(taskId))
        }
    }

  
  override def update(taskId: TaskId, update: TaskUpdate): IO[TaskModificationError | Throwable, Task] = 
    usingCollection { collection =>

      val modifications = List(
        update.name.map(name => BSONDocument("name" -> name.value)),
        update.description.map(description => BSONDocument("description" -> description.map(_.value).getOrElse(null))),
        update.status.map(status => BSONDocument("status" -> status.toString()))
      ).flatten

      
      ZIO.fromFuture(ec =>
        given ExecutionContext = ec
        collection.findAndUpdate(
          BSONDocument("_id" -> taskId.value),
          BSONDocument("$set" -> BSONDocument(modifications: _*)),
        )
      ).flatMap(r =>
         r.result[Task].map(update.applyOn) match {
          case Some(task) => ZIO.succeed(task)
          case None       => ZIO.fail(TaskModificationError.NotFoundError(taskId))
         }
      )
    }

  override def delete(taskId: TaskId): IO[TaskModificationError | Throwable, Unit] =
    usingCollection { collection =>
      ZIO.fromFuture(ec =>
        given ExecutionContext = ec
        collection.findAndRemove(BSONDocument("_id" -> taskId.value))
      ).flatMap(r =>
         r.value match {
          case Some(doc) => ZIO.succeed(())
          case None      => ZIO.fail(TaskModificationError.NotFoundError(taskId))
         }
      )
    }
}
