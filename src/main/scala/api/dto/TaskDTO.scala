package com.test.todoback.api.dto

import eu.timepit.refined.types.string.NonEmptyString
import io.circe.Codec
import io.circe.refined.*
import sttp.tapir.codec.refined.*
import sttp.tapir.Schema
import com.test.todoback.api.dto.TaskStatusDTO.*

final case class TaskDTO(
    id: String,
    name: NonEmptyString,
    description: Option[NonEmptyString],
    status: TaskStatusDTO
) derives Codec.AsObject, Schema

object TaskDTO:
    extension (task: com.test.todoback.model.Task)
        def toDTO: TaskDTO = TaskDTO(
            id = task.id.value,
            name = task.name,
            description = task.description,
            status = TaskStatusDTO(task.status)
        )
