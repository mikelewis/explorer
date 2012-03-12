package com.explorer.processor
import akka.actor.Actor
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import scala.collection.JavaConversions._

class HtmlProcessor extends Actor {
  def receive = {
    case ParseHtml(url, body) => sender ! parseHtml(url, body)
  }

  def parseHtml(url: String, body: String): DoneParseHtml = {
    val doc = Jsoup.parse(body, url)
    DoneParseHtml(doc, getUrlsFromDocument(doc))
  }

  def getUrlsFromDocument(doc: Document) = {
    doc.select("a[href],frame[src],link[href],script[src]").foldLeft(List.empty[String]) { (list, e) =>
      val url = e.tagName() match {
        case "a" | "link" => e.attr("abs:href")
        case "frame" | "script" => e.attr("abs:src")
      }
      url :: list
    }
  }
}