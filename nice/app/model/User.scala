package model

import play.api.libs.json.Json

case class User(
                 accountId: Int,
                 firstName: String,
                 lastName: String,
                 email: String
               )

object User {
  implicit val userFormat = Json.format[User]
}
