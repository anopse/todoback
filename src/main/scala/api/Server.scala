package com.test.todoback.api

import com.comcast.ip4s.Port
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import com.test.todoback.model.TaskCollection
import com.test.todoback.api.endpoint.Endpoints
import zio.stream.interop.fs2z.io.networkInstance
import java.io.EOFException
import zio.interop.catz.*
import zio.*

object Server:
  def run()(using TaskCollection): ZIO[Any, Any, Any] =

    val endpoints = Endpoints.allServer()
    val routes = ZHttp4sServerInterpreter().from(endpoints).toRoutes[Any]

    EmberServerBuilder
      .default[Task]
      .withoutHost
      .withPort(Port.fromInt(8080).get)
      .withHttpApp(Router("/" -> routes).orNotFound)
      .build
      .use { server =>
        for {
          _ <- Console.printLine(s"Server started at http://localhost:${server.address.getPort}. Press ENTER key to exit.")
          _ <- Console.readLine.catchSome {
            case _: EOFException =>
              for {
                _ <- Console.printLine("STDIN is unavailable, running forever...")
                _ <- ZIO.never
              } yield ()
          }
        } yield ()
      }