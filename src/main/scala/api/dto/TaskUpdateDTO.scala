package com.test.todoback.api.dto

import eu.timepit.refined.types.string.NonEmptyString
import io.circe.Codec
import io.circe.refined.*
import sttp.tapir.codec.refined.*
import sttp.tapir.Schema

final case class TaskUpdateDTO(
    name: Option[NonEmptyString],
    description: Option[Option[NonEmptyString]],
    status: Option[TaskStatusDTO]
) derives Codec.AsObject, Schema

object TaskUpdateDTO:
    extension (dto: TaskUpdateDTO)
        def toModel: com.test.todoback.model.TaskUpdate = com.test.todoback.model.TaskUpdate(
            name = dto.name,
            description = dto.description,
            status = dto.status.map(_.toModel)
        )
