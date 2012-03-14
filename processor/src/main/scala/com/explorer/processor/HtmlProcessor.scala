package com.explorer.processor
import akka.actor.Actor
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import scala.collection.JavaConversions._
import scala.io.Source._

class HtmlProcessor extends Actor {
  def receive = {
    case ParseHtml(url, body) => sender ! parseHtml(url, body)
  }

  def parseHtml(url: String, body: String): DoneParseHtml = {
    val doc = htmlToDocument(url, body)
    DoneParseHtml(doc, getUrlsFromDocument(doc))
  }

  def htmlToDocument(url: String, body: String) = Jsoup.parse(body, url)

  def getUrlsFromDocument(doc: Document) = {
    doc.select("a[href],iframe[src],link[href],script[src]").foldLeft(List.empty[String]) { (list, e) =>
      val url = e.tagName() match {
        case "a" | "link" => e.attr("abs:href")
        case "iframe" | "script" => e.attr("abs:src")
      }
      url :: list
    }
  }
}