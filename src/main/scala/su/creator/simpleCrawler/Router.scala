package su.creator.simpleCrawler

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import su.creator.simpleCrawler.crawler.Crawler
import su.creator.simpleCrawler.json.CrawlerJsonProtocol
import su.creator.simpleCrawler.model.{Request, Response}

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

class Router(crawler: Crawler) extends CrawlerJsonProtocol {

  implicit val timeout: Timeout = Timeout(3 seconds)

  val routes: Route = post {
    entity(as[Request]) { request ⇒
      onComplete(crawler.doJob(request)) {
        case Success(response: Response) ⇒
          complete(response)
        case Success(value) ⇒
          complete(value.toString)
        case Failure(exception) ⇒
          complete(exception)
      }
    }
  }

}
