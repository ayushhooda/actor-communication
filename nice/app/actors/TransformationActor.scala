package actors

import akka.actor.{Actor, ActorLogging}
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope
import javax.inject.Inject
import model.{TransformedUser, User}

class TransformationActor @Inject()(lookupBusImpl: LookupBusImpl) extends Actor with ActorLogging {
  override def receive: Receive = {
    case user: User =>
      log.info(s"Event received with accountId ${user.accountId}")
      processUser(user)
    case _ =>
      log.error(s"Unknown Event received!!!")
  }

  private def processUser(user: User): Unit = {
    val transformedUser = TransformedUser(user.accountId, user.firstName + " " + user.lastName, user.email)
    publishToLookupBus(transformedUser)
  }

  private def publishToLookupBus(transformedUser: TransformedUser): Unit = {
    log.info(s"Publish message to Printer Actor for user with account id ${transformedUser.accountId}")
    lookupBusImpl.publish(MsgEnvelope(PrinterActor.name, ConsistentHashableEnvelope(transformedUser, transformedUser.accountId)))
  }

}

object TransformationActor {
  val name = "transformation-actor"
  trait Factory {
    def apply(key: String): Actor
  }
}
