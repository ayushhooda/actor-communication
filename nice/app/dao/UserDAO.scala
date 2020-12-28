package dao

import javax.inject.{Inject, Singleton}
import model.User

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserDAO @Inject()(repo: UserRepo, dbImplicits: DBImplicits)(implicit ec: ExecutionContext){

  import dbImplicits.executeOperation

  def validateUser(accountId: Int): Future[Option[User]] = {
    repo.findByAccountId(accountId)
  }

}
