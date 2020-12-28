package controllers

import actors.{LookupBusImpl, MsgEnvelope, ValidationActor}
import javax.inject.{Inject, Singleton}
import model.User
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserController @Inject()(cc: ControllerComponents, lookupBusImpl: LookupBusImpl)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  /*
  Accept request (user data in form of JSON) from UI
   */
  def validateUser: Action[AnyContent] = Action.async { implicit request =>
    request.body.asJson.get.validate[User] match {
      case _: JsError =>
        Future(BadRequest(Json.obj("status" -> "Fail", "message" -> "Bad Request")))
      case s: JsSuccess[User] =>
        print(s"\n\nSuccessfully parsed json ${s.value}")
        lookupBusImpl.publish(MsgEnvelope(ValidationActor.name, s.value))
        Future(Ok)
    }
  }

}
