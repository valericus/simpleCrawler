package su.creator.simpleCrawler.crawler

import akka.actor.{Actor, ActorLogging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.Redirection
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.http.scaladsl.unmarshalling.FromResponseUnmarshaller
import akka.stream.{ActorMaterializer, Materializer}
import org.jsoup.nodes.Document
import su.creator.simpleCrawler.html.HtmlHelpers
import su.creator.simpleCrawler.json.JsoupUnmarshaller
import su.creator.simpleCrawler.model.{FailedItem, ResponseItem, SuccessfulItem}

import scala.concurrent.Future
import scala.language.{implicitConversions, postfixOps}

abstract class Crawler extends Actor with ActorLogging with HtmlHelpers {

  implicit def uriToRequest(uri: Uri): HttpRequest = HttpRequest(uri = uri)

  import context.{dispatcher, system}

  val http = Http()

  implicit val ma: Materializer = ActorMaterializer()(context)

  val unmarshall: FromResponseUnmarshaller[Document] = new JsoupUnmarshaller()

  /**
    * Proceed HttpRequest and follow redirects up to `maxRedirects` times.
    *
    * @param request      original request
    * @param count        current count of redirects, don't use it outside of recursion
    * @param maxRedirects maximal number of retries
    * @return final response or exception in case of infinite redirection loop
    */
  def requestWithRedirections(request: HttpRequest, count: Int = 0, maxRedirects: Int = 30): Future[HttpResponse] = {
    http.singleRequest(request) flatMap {
      case response@HttpResponse(redirection: Redirection, _, _, _) ⇒
        response.discardEntityBytes().future.flatMap { _ ⇒
          response.header[Location] match {
            case Some(Location(uri)) if count < maxRedirects ⇒
              requestWithRedirections(request.withUri(uri), count + 1, maxRedirects)
            case Some(Location(_)) ⇒
              Future.failed(new RuntimeException(s"Too many redirects ($count)"))
            case None ⇒
              Future.failed(new RuntimeException(s"$redirection without location header"))
          }
        }
      case response ⇒
        Future.successful(response)
    }
  }

  def dealWithUri(uri: Uri): Future[ResponseItem] = {
    requestWithRedirections(uri) flatMap { response ⇒
      if (response.status.isSuccess)
        unmarshall(response) flatMap { document ⇒
          Future(document / "head" / "title" text) map { title ⇒
            SuccessfulItem(uri, title)
          }
        }
      else {
        log.error("Bad response from URI {}: {}", uri, response.status)
        response.discardEntityBytes().future map { _ ⇒
          FailedItem(uri, s"Bad response: ${response.status}")
        }
      }
    }
  } recover {
    case exception: Exception ⇒
      log.error(exception, "Can't handle URI {}", uri)
      FailedItem(uri, exception.toString)
  }

}
