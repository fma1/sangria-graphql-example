package com.foo.graphql.query

import sangria.macros._

object TestUserQuery {
  val query = graphql"""
  query UserQuery {
    user(id: "23") {
      id,
      firstName,
      age
    }
  }
  """
}
