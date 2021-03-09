package com.foo.graphql

package object models {
  case class User(id: String, firstName: String, age: Int, companyId: Option[Int])
  case class Company(id: String, name: String, description: String)
  trait Identifiable {
    def id: String
  }
}
