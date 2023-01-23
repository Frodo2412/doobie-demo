package me.brunolemus.doobie

import cats.effect.{ExitCode, IO, IOApp}
import doobie.implicits._
import doobie.util.transactor.Transactor

object DoobieDemo extends IOApp {

  implicit class Debugger[A](io: IO[A]) {
    def debug: IO[A] = io map { a =>
      println(s"[${Thread.currentThread().getName}] $a")
      a
    }
  }

  val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:myimdb",
    "docker",
    "docker"
  )

  def findAllActorsNames: IO[List[String]] = {
    val query  = sql"SELECT name FROM actors".query[String]
    val action = query.to[List]
    action transact xa
  }

  override def run(args: List[String]): IO[ExitCode] = findAllActorsNames.debug as ExitCode.Success

}
