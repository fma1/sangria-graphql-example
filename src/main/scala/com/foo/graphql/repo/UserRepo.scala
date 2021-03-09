package com.foo.graphql.repo

import com.foo.graphql.models.User

class UserRepo {
  private val Users = List(
    User("23", "Bill", 20, Some(1)),
    User("40", "Alex", 40, Some(2)),
    User("41", "Nick", 40, Some(2)),
    User("f90o73u", "Stephen", 26, None)
  )

  def user(id: String): Option[User] =
    Users find (_.id.equals(id))

  def users: List[User] = Users
}
