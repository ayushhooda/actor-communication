package dao

import javax.inject.Inject
import model.User
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

class UserRepo @Inject()(dbConfigProvider: DatabaseConfigProvider) {

  val driver = dbConfigProvider.get[JdbcProfile].profile

  import driver.api._

  val tableQuery = TableQuery[UserTable]

  def findByAccountId(accountId: Int): DBIO[Option[User]] = {
    tableQuery.filter(_.accountId === accountId).result.headOption
  }

  class UserTable(tag: slick.lifted.Tag) extends Table[User](tag, "user") {
    def * : ProvenShape[User] = (accountId, firstName, lastName, email) <> ((User.apply _).tupled, User.unapply)

    def accountId: Rep[Int] = column[Int]("account_id", O.Unique)
    def firstName: Rep[String] = column[String]("first_name")
    def lastName: Rep[String] = column[String]("last_name")
    def email: Rep[String] = column[String]("email")

  }
}
