package su.creator.simpleCrawler

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import su.creator.simpleCrawler.crawler.NaiveCrawler

object SimpleCrawler extends App {

  implicit val system: ActorSystem = ActorSystem("SimpleCrawler")

  val crawler = new NaiveCrawler
  val router = new Router(crawler)

  Http().bindAndHandle(router.routes, "localhost", 8080)
}
