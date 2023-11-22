package com.test.todoback.api.endpoint

import sttp.tapir.*
import sttp.tapir.ztapir.ZServerEndpoint
import com.test.todoback.api.dto.TaskCreationDTO
import sttp.tapir.json.circe.*
import com.test.todoback.api.dto.*
import com.test.todoback.model.*
import com.test.todoback.api.dto.TaskDTO.*

object TaskCreateEndpoint {
  val public: PublicEndpoint[TaskCreationDTO, Unit, TaskDTO, Any] =
    endpoint.post
      .in("task" / "create")
      .in(jsonBody[TaskCreationDTO])
      .out(jsonBody[TaskDTO])

  def server(using collection: TaskCollection): ZServerEndpoint[Any, Any] =
    public.serverLogic { taskCreationDTO =>
      collection
        .create(
          name = taskCreationDTO.name,
          description = taskCreationDTO.description,
          status = taskCreationDTO.status.map(_.toModel).getOrElse(TaskStatus.Pending)
        )
        .map(model => Right(model.toDTO))
    }
}
