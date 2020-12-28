package dao

import javax.inject.Inject
import model.User

import scala.concurrent.{ExecutionContext, Future}

class UserService  @Inject()(
                              userDAO: UserDAO
                            )(implicit ec: ExecutionContext) {

  def validate(accountId: Int): Future[Option[User]] = {
    userDAO.validateUser(accountId)
  }

}
