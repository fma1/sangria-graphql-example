package com.foo.graphql.repo

import com.foo.graphql.models.Company

class CompanyRepo {
  private val Companies = List(
    Company("1", "Apple", "iphone"),
    Company("2", "Google", "search")
  )

  def company(id: String): Option[Company] =
    Companies find (_.id.equals(id))

  def companies: List[Company] = Companies
}
