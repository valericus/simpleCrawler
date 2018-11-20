package su.creator.simpleCrawler

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.{ActorMaterializer, Materializer}
import su.creator.simpleCrawler.crawler.NaiveCrawler

object SimpleCrawler extends App {

  implicit val system: ActorSystem = ActorSystem("SimpleCrawler")
  implicit val materializer: Materializer = ActorMaterializer()

  val crawler = system.actorOf(Props(new NaiveCrawler), "naiveCrawler")
  val router = new Router(crawler)

  Http().bindAndHandle(router.routes, "localhost", 8080)

}
