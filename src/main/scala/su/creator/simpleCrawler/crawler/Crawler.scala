package su.creator.simpleCrawler.crawler

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.Redirection
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.http.scaladsl.unmarshalling.FromResponseUnmarshaller
import com.typesafe.scalalogging.Logger
import org.jsoup.nodes.Document
import su.creator.simpleCrawler.html.HtmlHelpers
import su.creator.simpleCrawler.json.JsoupUnmarshaller
import su.creator.simpleCrawler.model._

import scala.concurrent.Future
import scala.util.control.NonFatal

abstract class Crawler(implicit as: ActorSystem) extends HtmlHelpers {

  def doJob(request: Request): Future[Response]

  import as.dispatcher

  private val log: Logger = Logger(getClass)

  private val http = Http()

  private val unmarshall: FromResponseUnmarshaller[Document] = new JsoupUnmarshaller()

  /**
    * Proceed HttpRequest and follow redirects up to `maxRedirects` times.
    *
    * @param request      original request
    * @param count        current count of redirects, don't use it outside of recursion
    * @param maxRedirects maximal number of retries
    * @return final response or exception in case of infinite redirection loop
    */
  private def requestWithRedirections(request: HttpRequest, count: Int = 0, maxRedirects: Int = 30): Future[HttpResponse] = {
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

  protected def dealWithUri(uri: Uri): Future[ResponseItem] = {
    requestWithRedirections(HttpRequest(uri = uri)) flatMap { response ⇒
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
    case NonFatal(throwable) ⇒
      log.error(s"Can't handle URI $uri", throwable)
      FailedItem(uri, throwable.toString)
  }

}
