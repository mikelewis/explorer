package com.explorer.fetcher
import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorSystem
import com.ning.http.client.AsyncHttpClient
import akka.routing.RoundRobinRouter

class Fetcher(fetchConfig: FetchConfig) extends Actor
  with akka.actor.ActorLogging {
  val httpClient = new AsyncHttpClient(fetchConfig.httpClientConfig)

  val urlWorkers = Vector.fill(5)(context.actorOf(Props(new UrlWorker(context.system, httpClient, fetchConfig))))
  val router = context.actorOf(Props(new UrlWorker(context.system, httpClient, fetchConfig)).withRouter(RoundRobinRouter(urlWorkers)))

  override def postStop() {
    httpClient.close
  }

  def receive = {
    case FetchUrl(host, url) => router ! FetchUrl(host, url)
    case completedFetch: CompletedFetch => handleCompletedFetch(completedFetch)
  }

  def handleCompletedFetch(completedFetch: CompletedFetch) {
    completedFetch match {
      case success: SucessfulFetch => handleSuccessFetch(success)
      case failure: FailedFetch => handleFailedFetch(failure)
    }

    handleDoneUrlWorker(completedFetch.host, completedFetch.url)
  }

  def handleSuccessFetch(success: SucessfulFetch) {
    log.info("Success! Fetch Header " + success.headers)
  }

  def handleFailedFetch(failed: FailedFetch) {
    log.info("Got a failed fetch " + failed)
  }

  def handleDoneUrlWorker(host: String, url: String) {
    // send to FetchedURLQueue
    // ack to queue
  }

}