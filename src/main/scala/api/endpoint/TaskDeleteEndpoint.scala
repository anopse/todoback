package com.test.todoback.api.endpoint

import sttp.tapir.*
import zio.ZIO
import sttp.tapir.ztapir.ZServerEndpoint
import sttp.tapir.json.circe.*
import com.test.todoback.api.dto.*
import com.test.todoback.model.*
import com.test.todoback.api.dto.TaskDTO.*
import sttp.model.StatusCode

object TaskDeleteEndpoint {
  val public: PublicEndpoint[String, ErrorDTO, Unit, Any] =
    endpoint.delete
      .in("task" / path[String])
      .errorOut(jsonBody[ErrorDTO])
      .errorOut(statusCode(StatusCode.NotFound))

  def server(using collection: TaskCollection): ZServerEndpoint[Any, Any] =
    public.serverLogic { case id =>
      collection
        .delete(
          taskId = TaskId(id)
        ).map[Either[ErrorDTO, Unit]](_ => Right(()))
        .catchAll[Any, Throwable, Either[ErrorDTO, Unit]] {
          case TaskModificationError.NotFoundError(taskId) => ZIO.succeed(Left(ErrorDTO.TaskByIdNotFound(taskId)))
          case t: Throwable => ZIO.fail(t)
        }
    }
}
