package com.foo.graphql

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError, OK}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import com.foo.graphql.GraphQLSchema.schema
import com.foo.graphql.repo.UserRepo
import sangria.ast.Document
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.marshalling.sprayJson._
import sangria.parser.QueryParser
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object GraphQLServer extends App {
  val PORT = 8080

  implicit val actorSystem: ActorSystem = ActorSystem("graphql-server")

  scala.sys.addShutdownHook(() -> shutdown())

  val route =
    (post & path("graphql")) {
      entity(as[JsValue]) { requestJson ⇒
        graphQLEndpoint(requestJson)
      }
    } ~
      get {
        getFromResource("graphiql.html")
      }

  Http().newServerAt("localhost", PORT).bind(route)
  println(s"open a browser with URL: http://localhost:$PORT")

  def shutdown(): Unit = {
    actorSystem.terminate()
    Await.result(actorSystem.whenTerminated, 30 seconds)
  }

  def graphQLEndpoint(requestJson: JsValue) = {
    val JsObject(fields) = requestJson

    val JsString(query) = fields("query")

    val operation = fields.get("operationName") collect {
      case JsString(op) ⇒ op
    }

    val vars = fields.get("variables") match {
      case Some(obj: JsObject) ⇒ obj
      case _ ⇒ JsObject.empty
    }

    QueryParser.parse(query) match {

      // query parsed successfully, time to execute it!
      case Success(queryAst) ⇒
        complete(executeGraphQLQuery(queryAst, operation, vars))

      // can't parse GraphQL query, return error
      case Failure(error) ⇒
        complete(BadRequest, JsObject("error" -> JsString(error.getMessage)))
    }
  }

  def executeGraphQLQuery(query: Document, op: Option[String], vars: JsObject) =
    Executor.execute(schema, query, new UserRepo, variables = vars, operationName = op)
      .map(OK -> _)
      .recover {
        case error: QueryAnalysisError ⇒ BadRequest -> error.resolveError
        case error: ErrorWithResolver ⇒ InternalServerError -> error.resolveError
      }
}
