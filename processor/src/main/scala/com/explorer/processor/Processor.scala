package com.explorer.processor
import akka.actor.Actor
import com.explorer.common.RegisterTrafficMan
import akka.actor.ActorRef
import com.explorer.common.BaseFetchedJsonObject
import com.explorer.common.JsonCompletedFetch
import akka.actor.Props
import akka.pattern.ask
import akka.pattern.pipeTo
import akka.util.Timeout
import akka.util.duration._
import org.jsoup.nodes.Document

class Processor(processorConfig: ProcessorConfig) extends Actor with akka.actor.ActorLogging {
  var trafficMan: ActorRef = _
  val htmlProcessor = context.actorOf(Props[HtmlProcessor])
  val urlProcessor = context.actorOf(Props(new UrlProcessor(processorConfig.redisConfig)))

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
    log.info("Processing page " + finalUrl + " status: " + status + " headers: " + headers)

    parseHtml(finalUrl, status, headers, body).foreach {
      case (document, urls) =>
        runParsedHtmlCallback(finalUrl, status, headers, document)
        urls.foreach { urlFound =>
          processUrl(urlFound).foreach {
            case Some((url, delay)) => sendUrlToUrlScheduler(url, delay)
            case None => println("\n\n\n\n\n CANT PROCEED WITH url: " + urlFound)
          }
        }
    }
  }

  def processUrl(url: String) = {
    log.info("Processing url " + url)
    urlProcessor.ask(ProcessUrl(url))(60 seconds).map {
      case ProceedWithUrl(url, delay) => Some((url, delay))
      case c: DoNotProceedWithUrl => None
    }
  }

  def parseHtml(url: String, status: Int, headers: Map[String, String], body: String) = {
    htmlProcessor.ask(ParseHtml(url, body))(5 seconds).map {
      case DoneParseHtml(document, urls) => (document, urls)
    }
  }

  def runParsedHtmlCallback(url: String, status: Integer, headers: Map[String, String], document: Document) {
    log.info("Going to send callback for " + url + " " + status + " " + headers + " " + document)
  }

  def handleNonSuccessfulFetch(nonValidResponse: ProcessCompletedFetchedUrl) {

  }

  def handleFailedFetch(failed: ProcessFailedFetchedUrl) {
    log.info("Failed fetch " + failed)
  }

  def sendUrlToUrlScheduler(url: String, crawlDelay: Int) {
    log.info("Going to send " + url + " with crawlDelay " + crawlDelay + " to Url Scheduler")
  }
}