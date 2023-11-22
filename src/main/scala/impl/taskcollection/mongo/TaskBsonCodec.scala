package com.test.todoback.impl.taskcollection.mongo

import com.test.todoback.model.Task
import eu.timepit.refined.types.string.NonEmptyString
import com.test.todoback.model.TaskId
import com.test.todoback.model.TaskStatus
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.BSONDocumentReader
import scala.util.Failure
import scala.util.Success
import reactivemongo.api.bson.BSONDocumentWriter

object TaskBsonCodec:
    given BSONDocumentReader[Task] = BSONDocumentReader.from(bson =>
        for {
            id <- bson.getAsTry[String]("_id")
            name <- bson.getAsTry[String]("name")
            nonEmptyName <- NonEmptyString.from(name) match {
                                case Right(nonEmpty) => Success(nonEmpty)
                                case Left(_) => Failure(new Exception("Name is empty"))
                            }
            description = bson.getAsOpt[String]("description")
            nonEmptyDescription <- description.map(NonEmptyString.from) match {
                                        case Some(Right(nonEmpty)) => Success(Some(nonEmpty))
                                        case Some(Left(_)) => Failure(new Exception("Description is empty"))
                                        case None => Success(None)
                                    }
            status <- bson.getAsTry[String]("status")
        } yield Task(TaskId(id), nonEmptyName, nonEmptyDescription, TaskStatus.unsafeFrom(status))
    )

    given BSONDocumentWriter[Task] = BSONDocumentWriter(task =>
        BSONDocument(
            "_id" -> task.id.value,
            "name" -> task.name.value,
            "description" -> task.description.map(_.value),
            "status" -> task.status.toString
        )
    )
            

