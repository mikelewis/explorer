package com.explorer.processor

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.EitherValues._
import org.scalatest.BeforeAndAfterEach
import com.explorer.common.RunTestServer
import org.jsoup.nodes.Document
import org.jsoup.Jsoup

@RunWith(classOf[JUnitRunner])
class HtmlProcessorSpec extends MasterSuite
  with BeforeAndAfterEach {
  var file = contentsOfSampleFile("1.html")
  var (htmlProcessorRef, htmlProcessorActor) = testHtmlProcessor

  override def beforeEach() {
    file = contentsOfSampleFile("1.html")
  }

  describe("parseHtml") {
    it("should return a DoneParseHtml") {
      htmlProcessorActor.parseHtml("http://localhost", file).isInstanceOf[DoneParseHtml] should be(true)
    }

    it("should return a DoneParseHtml with a doc and a list of urls") {
      val result = htmlProcessorActor.parseHtml("http://localhost", file)
      val DoneParseHtml(doc, urls) = result
      doc.isInstanceOf[Document] should be(true)
      urls.isInstanceOf[List[String]] should be(true)
    }
  }

  describe("htmlToDocument") {
    it("should return a document") {
      val result = htmlProcessorActor.htmlToDocument("http://localhost", file)
      result.isInstanceOf[Document] should be(true)
      result.outerHtml should be(Jsoup.parse(file, "http://localhost").outerHtml)
    }
  }

  describe("getUrlsFromDocument") {
    it("should return a list of urls (strings)") {
      val doc = htmlProcessorActor.htmlToDocument("http://localhost", file)
      htmlProcessorActor.getUrlsFromDocument(doc).isInstanceOf[List[String]] should be(true)
    }

    it("should grab basic links (a href)") {
      val doc = htmlProcessorActor.htmlToDocument("http://localhost", file)
      htmlProcessorActor.getUrlsFromDocument(doc).toSet should be(Array("http://localhost/2.html", "http://localhost/3.html").toSet)
    }

    it("should grab other links") {
      var file = contentsOfSampleFile("other_links.html")
      val doc = htmlProcessorActor.htmlToDocument("http://localhost", file)
      htmlProcessorActor.getUrlsFromDocument(doc).toSet should be(Array("http://localhost/yus.css", "http://google.com/script.js", "http://localhost/default.asp").toSet)

    }
  }
}