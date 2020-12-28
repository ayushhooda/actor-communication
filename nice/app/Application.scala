import actors.{DirectorActor, PrinterActor, TransformationActor, ValidationActor}
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

class Application extends AbstractModule with AkkaGuiceSupport {

  override def configure(): Unit = {
    bindActor[DirectorActor]("director-actor")
    bindActorFactory[ValidationActor, ValidationActor.Factory]
    bindActorFactory[TransformationActor, TransformationActor.Factory]
    bindActorFactory[PrinterActor, PrinterActor.Factory]
  }

}
