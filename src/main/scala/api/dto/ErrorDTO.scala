package com.test.todoback.api.dto

import io.circe.Codec
import sttp.tapir.Schema
import com.test.todoback.model.TaskId
import com.test.todoback.model.TaskModificationError

final case class ErrorDTO(
    errorId: String,
    message: String,
    data: Option[String] = None
) derives Codec.AsObject, Schema

object ErrorDTO:
    def TaskByIdNotFound(id: TaskId) = ErrorDTO(
        errorId = "TaskByIdNotFound",
        message = "Task does not exist",
        data = Some(id.value)
    )

    def from(error: TaskModificationError) = error match {
        case TaskModificationError.NotFoundError(taskId) => TaskByIdNotFound(taskId)
    }