package com.foo.graphql

import com.foo.graphql.models.{Company, Identifiable, User}
import com.foo.graphql.repo.UserRepo
import sangria.macros.derive.deriveObjectType
import sangria.schema._

object GraphQLSchema {
  val UserType: ObjectType[Unit, User] = deriveObjectType[Unit, User]()
  val CompanyType: ObjectType[Unit, Company] = deriveObjectType[Unit, Company]()

  val IdentifiableType: InterfaceType[Unit, Identifiable] = InterfaceType(
    "Identifiable",
    "Entity that can be identified",

    fields[Unit, Identifiable](
      Field("id", StringType, resolve = _.value.id)))

  val Id: Argument[String] = Argument("id", StringType)

  val QueryType: ObjectType[UserRepo, Unit] = ObjectType("Query", fields[UserRepo, Unit](
    Field("user", OptionType(UserType),
      description = Some("Returns a user with specific `id`."),
      arguments = Id :: Nil,
      resolve = c ⇒ c.ctx.user(c arg Id)),

    Field("users", ListType(UserType),
      description = Some("Returns a list of all users."),
      resolve = _.ctx.users)))

  val schema: Schema[UserRepo, Unit] = Schema(QueryType)
}
