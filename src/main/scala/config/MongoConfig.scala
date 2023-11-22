package com.test.todoback.config

import com.typesafe.config.ConfigFactory
import scala.util.Try

final case class MongoConfig(
    host: String,
    port: Int,
    database: String,
    authDatabase: String,
    username: String,
    password: String
)

object MongoConfig:
    def load(): Option[MongoConfig] =
        val rootPath = "app.database.mongodb"
        val mongoConfigTry = Try {
            val config = ConfigFactory.load()
            val host = config.getString(s"$rootPath.host")
            val port = config.getInt(s"$rootPath.port")
            val database = config.getString(s"$rootPath.database")
            val authDatabase = config.getString(s"$rootPath.authDatabase")
            val username = config.getString(s"$rootPath.username")
            val password = config.getString(s"$rootPath.password")
            MongoConfig(host, port, database, authDatabase, username, password)
        } 

        mongoConfigTry.toOption
