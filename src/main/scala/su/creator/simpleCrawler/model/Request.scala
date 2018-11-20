package su.creator.simpleCrawler.model

import akka.http.scaladsl.model.Uri

case class Request(uris: Seq[Uri])
