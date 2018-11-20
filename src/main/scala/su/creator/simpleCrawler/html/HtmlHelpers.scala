package su.creator.simpleCrawler.html

import org.jsoup.nodes.Element

trait HtmlHelpers {

  implicit class ElementHelper(element: Element) {

    def /(tag: String): Element = element.getElementsByTag(tag).first

  }

}
