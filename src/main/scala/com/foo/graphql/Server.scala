package com.foo.graphql

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import com.foo.graphql.GraphQLSchema.schema
import com.foo.graphql.query.TestUserQuery
import com.foo.graphql.repo.UserRepo
import sangria.execution._
import sangria.marshalling.circe._
import io.circe.Json

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Server extends App {
  val PORT = 8080

  /*
  implicit val actorSystem: ActorSystem = ActorSystem("graphql-server")

  scala.sys.addShutdownHook(() -> shutdown())

  val route = complete("Hello GraphQL Scala!!!")

  Http().newServerAt("localhost", PORT).bind(route)
  println(s"open a browser with URL: http://localhost:$PORT")
   */

  val result: Future[Json] = Executor.execute(schema, TestUserQuery.query, new UserRepo)

  result onComplete {
    case Success(json) =>
      println(json.spaces2SortKeys)
    case Failure(exception) =>
      System.err.println(exception)
  }

  Thread.sleep(20000)

  /*
  def shutdown(): Unit = {
    actorSystem.terminate()
    Await.result(actorSystem.whenTerminated, 30 seconds)
  }
   */
}
