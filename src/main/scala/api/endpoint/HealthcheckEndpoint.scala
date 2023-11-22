package com.test.todoback.api.endpoint

import sttp.tapir.*
import sttp.tapir.ztapir.ZServerEndpoint
import zio.ZIO

object HealthcheckEndpoint {
  val public: PublicEndpoint[Unit, Unit, String, Any] =
    endpoint.get
      .in("health")
      .out(stringBody)

  def server: ZServerEndpoint[Any, Any] =
    public.serverLogic { _ =>
      ZIO.succeed(Right("Running !"))
    }
}
