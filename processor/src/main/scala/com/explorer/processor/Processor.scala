package com.explorer.processor
import akka.actor.Actor
import com.explorer.common.RegisterTrafficMan
import akka.actor.ActorRef
import com.explorer.common.BaseFetchedJsonObject
import com.explorer.common.JsonCompletedFetch
import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import akka.util.duration._
import org.jsoup.nodes.Document

class Processor extends Actor with akka.actor.ActorLogging {
  var trafficMan: ActorRef = _
  val htmlProcessor = context.actorOf(Props[HtmlProcessor])
  val urlProcessor = context.actorOf(Props[UrlProcessor])

  def receive = {
    case RegisterTrafficMan => trafficMan = sender
    case completed: ProcessCompletedFetchedUrl => handleCompletedFetch(completed)
    case failed: ProcessFailedFetchedUrl => handleFailedFetch(failed)
  }

  def handleCompletedFetch: PartialFunction[ProcessCompletedFetchedUrl, Unit] = {
    case ProcessCompletedFetchedUrl(originalMsg, originalUrl, finalUrl,
      status, headers, body) if status == 200 =>
      startProcessing(finalUrl, status, headers, body)
    case nonValidResponse => handleNonSuccessfulFetch(nonValidResponse) // status is not 200
  }

  def startProcessing(finalUrl: String, status: Int, headers: Map[String, String], body: String) {
    val f = for {
      urls <- parseHtml(finalUrl, status, headers, body)
      processedUrls <- processUrls(urls)
    } yield processedUrls

    f.foreach { urls =>
      urls.foreach { case (url, delay) => sendUrlToUrlScheduler(url, delay) }
    }
  }

  def processUrls(urls: List[String]) = {
    urlProcessor.ask(ProcessUrls(urls))(60 seconds).map {
      case DoneProcessUrls(urls) => urls
    }
  }

  def parseHtml(url: String, status: Int, headers: Map[String, String], body: String) = {
    htmlProcessor.ask(ParseHtml(url, body))(5 seconds).map {
      case DoneParseHtml(document, urls) =>
        runParsedHtmlCallback(url, status, headers, document)
        urls
    }
  }

  def runParsedHtmlCallback(url: String, status: Integer, headers: Map[String, String], document: Document) {
    log.info("Going to send callback for " + url + " " + status + " " + headers + " " + document)
  }

  def handleNonSuccessfulFetch(nonValidResponse: ProcessCompletedFetchedUrl) {

  }

  def handleFailedFetch(failed: ProcessFailedFetchedUrl) {

  }

  def sendUrlToUrlScheduler(url: String, crawlDelay: Int) {
    log.info("GOing to send " + url + " with crawlDelay " + crawlDelay + " to Url Scheduler")
  }
}