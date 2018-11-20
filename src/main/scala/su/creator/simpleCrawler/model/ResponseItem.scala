package su.creator.simpleCrawler.model

import akka.http.scaladsl.model.Uri

trait ResponseItem {
  val uri: Uri
}

case class SuccessfulItem(uri: Uri, title: String) extends ResponseItem

case class FailedItem(uri: Uri, error: String) extends ResponseItem
