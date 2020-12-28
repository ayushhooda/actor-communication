package actors

import akka.actor.{Actor, ActorLogging}
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import dao.UserService
import javax.inject.Inject
import model.User

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class ValidationActor  @Inject()(lookupBusImpl: LookupBusImpl, userService: UserService)(implicit ec: ExecutionContext) extends Actor with ActorLogging {

  override def receive: Receive = {
    case user: User =>
      validateUser(user) onComplete {
        case Success(value) =>
          value.map(publishUserToEventBus)
        case Failure(_) =>
          log.error(s"User does not exist in DB")
      }
    case _ => log.warning(s"Unknown message received")
  }

  private def validateUser(user: User): Future[Option[User]] = {
    userService.validate(user.accountId)
  }

  private def publishUserToEventBus(user: User): Unit = {
    log.info(s"Publish message to Transformation Actor for user with account id ${user.accountId}")
    lookupBusImpl.publish(MsgEnvelope(TransformationActor.name, ConsistentHashableEnvelope(user, user.accountId)))
  }
}

object ValidationActor {
  val name = "validation-actor"

  trait Factory {
    def apply(key: String): Actor
  }
}
