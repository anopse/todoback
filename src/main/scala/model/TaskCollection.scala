package com.test.todoback.model

import eu.timepit.refined.types.string.NonEmptyString
import zio.IO

enum TaskModificationError:
    case NotFoundError(id: TaskId)

trait TaskCollection:
    def all(): IO[Throwable, List[Task]]

    def create(name: NonEmptyString, description: Option[NonEmptyString], status: TaskStatus): IO[Throwable, Task]

    def update(taskId: TaskId, update: TaskUpdate): IO[TaskModificationError | Throwable, Task]

    def delete(taskId: TaskId): IO[TaskModificationError | Throwable, Unit]

    def get(taskId: TaskId): IO[TaskModificationError | Throwable, Task]