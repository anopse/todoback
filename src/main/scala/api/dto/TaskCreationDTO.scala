package com.test.todoback.api.dto

import eu.timepit.refined.types.string.NonEmptyString
import io.circe.Codec
import io.circe.refined.*
import sttp.tapir.codec.refined.*
import sttp.tapir.Schema

final case class TaskCreationDTO(
    name: NonEmptyString,
    description: Option[NonEmptyString],
    status: Option[TaskStatusDTO]
) derives Codec.AsObject, Schema
