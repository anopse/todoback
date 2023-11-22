package com.test.todoback

import com.test.todoback.model.TaskCollection
import com.test.todoback.impl.taskcollection.InMemoryTaskCollection
import com.test.todoback.config.MongoConfig
import com.test.todoback.api.Server
import com.test.todoback.impl.taskcollection.MongoTaskCollection
import zio._

def loadTaskCollection(): TaskCollection =
  config.MongoConfig.load() match {
    case Some(conf) =>
      given MongoConfig = conf
      println("Mongodb config loaded, using MongoTaskCollection")
      MongoTaskCollection()
    case None =>
      println("/!\\ Failed to load Mongodb config, using InMemoryTaskCollection /!\\")
      InMemoryTaskCollection()
  }

@main def hello: Unit =
  given TaskCollection = loadTaskCollection()
  val runtime = Runtime.default
  Unsafe.unsafe(unsafe =>
    given Unsafe = unsafe
    runtime.unsafe.run(Server.run())
  ) match {
    case Exit.Success(_) => println("Server stopped without error")
    case Exit.Failure(cause) => println(s"Server stopped with error: $cause")
  }

