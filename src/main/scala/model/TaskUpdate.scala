package com.test.todoback.model

import eu.timepit.refined.types.string.NonEmptyString

case class TaskUpdate(
    name: Option[NonEmptyString],
    description: Option[Option[NonEmptyString]],
    status: Option[TaskStatus]
)

object TaskUpdate:
    extension (taskUpdate: TaskUpdate)
        def applyOn(task: Task): Task =
            task.copy(
                name = taskUpdate.name.getOrElse(task.name),
                description = taskUpdate.description.getOrElse(task.description),
                status = taskUpdate.status.getOrElse(task.status)
            )