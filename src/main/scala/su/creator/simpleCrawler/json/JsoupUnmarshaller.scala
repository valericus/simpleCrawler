package su.creator.simpleCrawler.json

import java.io.InputStream

import akka.http.scaladsl.model.HttpCharsets.`UTF-8`
import akka.http.scaladsl.model.{HttpCharset, HttpResponse}
import akka.http.scaladsl.unmarshalling.FromResponseUnmarshaller
import akka.stream.Materializer
import akka.stream.scaladsl.StreamConverters._
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.concurrent.{ExecutionContext, Future}

class JsoupUnmarshaller(defaultCharset: HttpCharset = `UTF-8`) extends FromResponseUnmarshaller[Document] {

  def cleanInputStream(is: InputStream, response: HttpResponse)(implicit ec: ExecutionContext): Document =
    Jsoup.parse(is, response.entity.contentType.charsetOption.getOrElse(defaultCharset).value, "")

  override def apply(response: HttpResponse)(implicit ec: ExecutionContext, materializer: Materializer): Future[Document] = {
    val inputStream = response.entity.dataBytes.runWith(asInputStream())
    Future(cleanInputStream(inputStream, response))
  }
}
