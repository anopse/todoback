package com.test.todoback.impl.taskcollection

import com.test.todoback.model.*
import zio.{ZIO, IO}
import com.test.todoback.model.TaskModificationError
import eu.timepit.refined.types.string.NonEmptyString
import scala.collection.mutable.Map as MutableMap

final class InMemoryTaskCollection extends TaskCollection {
  private type TaskMap = MutableMap[TaskId, Task]

  private val _tasks: TaskMap = MutableMap.empty

  private def usingTasks[T](f: TaskMap => T): T = _tasks.synchronized {
    f(_tasks)
  }

  override def all(): IO[Throwable, List[Task]] = usingTasks(tasks => ZIO.succeed(tasks.values.toList))

  override def create(
      name: NonEmptyString,
      description: Option[NonEmptyString],
      status: TaskStatus
  ): IO[Throwable, Task] = usingTasks { tasks =>
    val id = java.util.UUID.randomUUID().toString
    val task = Task(TaskId(id), name, description, status)

    tasks.put(task.id, task)
    ZIO.succeed(task)
  }

  override def get(taskId: TaskId): IO[TaskModificationError | Throwable, Task] =
    usingTasks { tasks =>
      tasks.get(taskId) match {
        case Some(task) => ZIO.succeed(task)
        case None       => ZIO.fail(TaskModificationError.NotFoundError(taskId))
      }
    }

  
  override def update(taskId: TaskId, update: TaskUpdate): IO[TaskModificationError | Throwable, Task] = usingTasks { tasks =>
    tasks.get(taskId) match {
      case Some(task) =>
        val updatedTask = update.applyOn(task)
        tasks.put(taskId, updatedTask)
        ZIO.succeed(updatedTask)
      case None => ZIO.fail(TaskModificationError.NotFoundError(taskId))
    }
  }

  override def delete(taskId: TaskId): IO[TaskModificationError | Throwable, Unit] =
    usingTasks { tasks =>
      tasks.get(taskId) match {
        case Some(_) =>
          tasks.remove(taskId)
          ZIO.succeed(())
        case None => ZIO.fail(TaskModificationError.NotFoundError(taskId))
      }
    }

}
