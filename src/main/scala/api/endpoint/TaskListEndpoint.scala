package com.test.todoback.api.endpoint

import sttp.tapir.*
import sttp.tapir.ztapir.ZServerEndpoint
import sttp.tapir.json.circe.*
import com.test.todoback.api.dto.*
import com.test.todoback.model.*
import com.test.todoback.api.dto.TaskDTO.*

object TaskListEndpoint {
  val public: PublicEndpoint[Unit, Unit, List[TaskDTO], Any] =
    endpoint.get
      .in("task" / "list")
      .out(jsonBody[List[TaskDTO]])

  def server(using collection: TaskCollection): ZServerEndpoint[Any, Any] =
    public.serverLogic { _ =>
      collection.all().map(list => Right(list.map(_.toDTO)))
    }
}
