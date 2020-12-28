package actors

import akka.actor.ActorRef
import akka.event.{EventBus, LookupClassification}
import javax.inject.Singleton

final case class MsgEnvelope(topic: String, payload: Any)

@Singleton
class LookupBusImpl extends EventBus with LookupClassification {
  override type Event = MsgEnvelope
  override type Classifier = String
  override type Subscriber = ActorRef

  override protected def mapSize(): Int = 128

  override protected def compareSubscribers(a: Subscriber, b: Subscriber): Int = {
    a.compareTo(b)
  }

  override protected def classify(event: Event): String = {
    event.topic
  }

  override protected def publish(event: Event, subscriber: Subscriber): Unit = {
    subscriber ! event.payload
  }
}
