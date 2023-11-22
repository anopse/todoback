package com.test.todoback.model

import eu.timepit.refined.types.string.NonEmptyString

final case class Task(
    id: TaskId,
    name: NonEmptyString,
    description: Option[NonEmptyString],
    status: TaskStatus
)
