package su.creator.simpleCrawler.crawler

import akka.actor.Props
import akka.pattern.pipe
import su.creator.simpleCrawler.model.{Request, Response}

import scala.concurrent.Future
import scala.language.{implicitConversions, postfixOps}

class NaiveCrawler extends Crawler {

  import context.dispatcher

  override def receive: Receive = {
    case Request(uris) ⇒
      Future.sequence(uris.map(dealWithUri)) map Response pipeTo sender
    case message ⇒
      log.error(s"Received unexpected message $message")
  }
}

object NaiveCrawler {

  def props: Props = Props(new NaiveCrawler)

}