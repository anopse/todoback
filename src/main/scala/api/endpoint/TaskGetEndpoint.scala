package com.test.todoback.api.endpoint

import sttp.tapir.*
import zio.ZIO
import sttp.tapir.ztapir.ZServerEndpoint
import sttp.tapir.json.circe.*
import com.test.todoback.api.dto.*
import com.test.todoback.model.*
import com.test.todoback.api.dto.TaskDTO.*
import sttp.model.StatusCode

object TaskGetEndpoint {
  val public: PublicEndpoint[String, ErrorDTO, TaskDTO, Any] =
    endpoint.get
      .in("task" / path[String])
      .out(jsonBody[TaskDTO])
      .errorOut(jsonBody[ErrorDTO])
      .errorOut(statusCode(StatusCode.NotFound))

  def server(using collection: TaskCollection): ZServerEndpoint[Any, Any] =
    public.serverLogic { case id =>
      collection
        .get(taskId = TaskId(id))
        .map[Either[ErrorDTO, TaskDTO]](model => Right(model.toDTO))
        .catchAll[Any, Throwable, Either[ErrorDTO, TaskDTO]] {
          case TaskModificationError.NotFoundError(taskId) => ZIO.succeed(Left(ErrorDTO.TaskByIdNotFound(taskId)))
          case t: Throwable => ZIO.fail(t)
        }
    }
}
