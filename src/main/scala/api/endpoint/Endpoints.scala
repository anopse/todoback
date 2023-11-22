package com.test.todoback.api.endpoint

import sttp.tapir.*
import sttp.tapir.ztapir.ZServerEndpoint
import com.test.todoback.model.TaskCollection

object Endpoints:
  def allServer()(using TaskCollection): List[ZServerEndpoint[Any, Any]] = List(
    TaskCreateEndpoint.server,
    TaskListEndpoint.server,
    TaskUpdateEndpoint.server,
    TaskDeleteEndpoint.server,
    TaskGetEndpoint.server,
    HealthcheckEndpoint.server
  )
