package su.creator.simpleCrawler.json

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.Uri
import spray.json._
import su.creator.simpleCrawler.model._

trait CrawlerJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {

  implicit object UriFormat extends RootJsonFormat[Uri] {
    override def write(uri: Uri): JsValue = JsString(uri.toString)

    override def read(json: JsValue): Uri = try Uri(json.convertTo[String]) catch {
      case exception: Exception ⇒
        deserializationError("Invalid URI", exception)
    }
  }

  implicit val requestFormat: RootJsonFormat[Request] = jsonFormat1(Request)

  implicit val successfulItemFormat: RootJsonFormat[SuccessfulItem] = jsonFormat2(SuccessfulItem)
  implicit val failedItemFormat: RootJsonFormat[FailedItem] = jsonFormat2(FailedItem)

  implicit object ResponseItemFormat extends RootJsonFormat[ResponseItem] {
    override def write(obj: ResponseItem): JsValue = obj match {
      case successfulItem: SuccessfulItem ⇒ successfulItem.toJson
      case failedItem: FailedItem ⇒ failedItem.toJson
    }

    // actually we need no reader but full format is required by marshaller
    override def read(json: JsValue): ResponseItem =
      if (json.asJsObject.fields contains "title")
        json.convertTo[SuccessfulItem]
      else
        json.convertTo[FailedItem]

  }

  implicit val responseFormat: RootJsonFormat[Response] = jsonFormat1(Response)
}
