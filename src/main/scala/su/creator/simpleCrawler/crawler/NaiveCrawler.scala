package su.creator.simpleCrawler.crawler

import akka.actor.ActorSystem
import su.creator.simpleCrawler.model.{Request, Response}

import scala.concurrent.Future
import scala.language.{implicitConversions, postfixOps}

class NaiveCrawler(implicit as: ActorSystem) extends Crawler {

  import as.dispatcher

  def doJob(request: Request): Future[Response] = {
    Future.traverse(request.uris)(dealWithUri).map(Response)
  }
}