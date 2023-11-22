package com.test.todoback.model

opaque type TaskId = String

object TaskId:
  inline def apply(id: String): TaskId = id

  extension (taskId: TaskId)
    inline def value: String = taskId

