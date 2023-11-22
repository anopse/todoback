package com.test.todoback.api.dto

import sttp.tapir.Schema
import org.latestbit.circe.adt.codec.JsonTaggedAdt
import com.test.todoback.model.TaskStatus

enum TaskStatusDTO derives JsonTaggedAdt.PureCodec, Schema:
  case Pending
  case Done

object TaskStatusDTO:
    def apply(status: TaskStatus): TaskStatusDTO = status match
        case TaskStatus.Pending => Pending
        case TaskStatus.Done => Done

    extension (dto: TaskStatusDTO)
        def toModel: TaskStatus = dto match
            case Pending => TaskStatus.Pending
            case Done => TaskStatus.Done