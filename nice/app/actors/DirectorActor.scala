package actors

import java.util.concurrent.TimeUnit.{MINUTES, SECONDS}

import actors.DirectorActor.InitializeDirector
import akka.actor.SupervisorStrategy.Resume
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Terminated, Timers}
import akka.routing.FromConfig
import javax.inject.{Inject, Singleton}
import play.api.libs.concurrent.InjectedActorSupport

import scala.concurrent.duration.FiniteDuration

@Singleton
class DirectorActor @Inject() (
                                validationActorFactory: ValidationActor.Factory,
                                transformationActorFactory: TransformationActor.Factory,
                                printerActorFactory: PrinterActor.Factory,
                                lookupBusImpl: LookupBusImpl
                              ) extends Actor with ActorLogging with InjectedActorSupport with Timers {

  log.info(s"Started DirectorActor: ${this.self.path}.")

  val scheduledInitTime = new FiniteDuration(10, SECONDS)

  override val supervisorStrategy: OneForOneStrategy = OneForOneStrategy(maxNrOfRetries = -1, withinTimeRange = DirectorActor.supervisorTimeRange) {
    case _: Exception => Resume
  }

  var isInitialized = false

  timers.startSingleTimer("init", InitializeDirector, scheduledInitTime)

  log.info("DirectorActor is launched")

  override def receive: Receive = {
    case InitializeDirector => init()
    case Terminated => log.error(s"Child Actor Terminated.")
    case _ => log.warning("Unknown message received.")
  }

  private def init(): Unit = {

    if (!isInitialized) {
      launchValidationActor
      launchTransformationActor
      launchPrinterActor
      isInitialized = true
    }

  }

  private def launchValidationActor: Boolean = {
    val validationActorManager = injectedChild(validationActorFactory(ValidationActor.name), ValidationActor.name, FromConfig.withSupervisorStrategy(supervisorStrategy).props)
    context.watch(validationActorManager)
    lookupBusImpl.subscribe(validationActorManager, ValidationActor.name)
  }

  private def launchTransformationActor: Boolean = {
    val transformerActorManager = injectedChild(transformationActorFactory(TransformationActor.name), TransformationActor.name, FromConfig.withSupervisorStrategy(supervisorStrategy).props)
    context.watch(transformerActorManager)
    lookupBusImpl.subscribe(transformerActorManager, TransformationActor.name)
  }

  private def launchPrinterActor: Boolean = {
    val printerActorManager = injectedChild(printerActorFactory(PrinterActor.name), PrinterActor.name, FromConfig.withSupervisorStrategy(supervisorStrategy).props)
    context.watch(printerActorManager)
    lookupBusImpl.subscribe(printerActorManager, PrinterActor.name)
  }
}

object DirectorActor {
  val supervisorTimeRange = new FiniteDuration(5, MINUTES)
  case object InitializeDirector
}
