package com.test.todoback.model

enum TaskStatus:
  case Pending
  case Done

object TaskStatus:
  def from(str: String): Option[TaskStatus] =
    str match
      case "Pending" => Some(TaskStatus.Pending)
      case "Done" => Some(TaskStatus.Done)
      case _ => None

  def unsafeFrom(str: String): TaskStatus =
    from(str).getOrElse(throw new IllegalArgumentException(s"Invalid TaskStatus: $str"))