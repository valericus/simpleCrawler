package su.creator.simpleCrawler.json

import akka.http.scaladsl.model.Uri
import io.circe.Decoder.Result
import io.circe._
import io.circe.syntax._

import scala.util.{Failure, Success, Try}

trait CrawlerJsonProtocol {

  implicit object UriCoder extends Encoder[Uri] with Decoder[Uri] {
    override def apply(uri: Uri): Json = uri.toString.asJson

    override def apply(cursor: HCursor): Result[Uri] = {
      cursor.as[String].flatMap { string =>
        Try(Uri(string)) match {
          case Failure(exception) =>
            Left(DecodingFailure(exception.getMessage, Nil))
          case Success(uri) =>
            Right(uri)
        }
      }
    }
  }

}
