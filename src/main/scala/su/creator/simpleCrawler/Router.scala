package su.creator.simpleCrawler

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import su.creator.simpleCrawler.crawler.Crawler
import su.creator.simpleCrawler.json.CrawlerJsonProtocol
import su.creator.simpleCrawler.model.Request

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

class Router(crawler: Crawler) extends CrawlerJsonProtocol with FailFastCirceSupport {

  implicit val timeout: Timeout = Timeout(3 seconds)

  val routes: Route = post {
    entity(as[Request]) { request ⇒
      onComplete(crawler.doJob(request)) {
        case Success(response) ⇒
          complete(response)
        case Failure(exception) ⇒
          complete(exception)
      }
    }
  }

}
