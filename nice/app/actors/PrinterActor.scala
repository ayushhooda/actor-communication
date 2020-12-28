package actors

import akka.actor.Actor
import model.TransformedUser

class PrinterActor extends Actor {
  override def receive: Receive = {
    case user: TransformedUser =>
      print(s"\n\nTransformed Data: $user")
  }
}

object PrinterActor {

  val name = "printer-actor"

  trait Factory {
    def apply(key: String): Actor
  }
}
