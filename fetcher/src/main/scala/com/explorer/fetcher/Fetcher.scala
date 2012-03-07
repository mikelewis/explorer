package com.explorer.fetcher
import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorSystem
import com.ning.http.client.AsyncHttpClient
import akka.routing.RoundRobinRouter
import net.fyrie.redis._
import akka.actor.ActorRef
import com.explorer.common.RegisterTrafficMan
import com.explorer.common.AckMessage

class Fetcher(fetchConfig: FetchConfig) extends Actor
  with akka.actor.ActorLogging {
  val httpClient = new AsyncHttpClient(fetchConfig.httpClientConfig)

  val urlWorkers = Vector.fill(fetchConfig.numUrlWorkers)(context.actorOf(Props(new UrlWorker(httpClient, fetchConfig))))
  val router = context.actorOf(Props(new UrlWorker(httpClient, fetchConfig)).withRouter(RoundRobinRouter(urlWorkers)))

  val r = RedisClient(SystemSettings.config.redisHost, SystemSettings.config.redisPort)(context.system)

  var trafficMan: ActorRef = _

  override def postStop() {
    httpClient.close
    r.disconnect
  }

  def receive = {
    case FetchUrl(url) => router ! DownloadUrl(url)
    case completedFetch: CompletedFetch => handleCompletedFetch(completedFetch)
    case RegisterTrafficMan => trafficMan = sender
  }

  def handleCompletedFetch(completedFetch: CompletedFetch) {
    completedFetch match {
      case success: SuccessfulFetch => handleSuccessFetch(success)
      case failure: FailedFetch => handleFailedFetch(failure)
    }

    handleDoneUrlWorker(completedFetch)
  }

  def handleSuccessFetch(success: SuccessfulFetch) {
    val json = JsonHelper.prepareForFetchedUrlQueue(success)
    log.info("Preparing to send successful fetch to fetched_url queue " +
      json)
    pushToFetchedUrlQueue(json)
  }

  def handleFailedFetch(failed: FailedFetch) {
    val json = JsonHelper.prepareForFetchedUrlQueue(failed)
    log.info("Preparing to send failed fetch to fetched_url queue " +
      json)
    pushToFetchedUrlQueue(json)
  }

  def pushToFetchedUrlQueue(json: String) {
    r.quiet.lpush(SystemSettings.config.redisFetchedUrlQueue, json)
  }

  def handleDoneUrlWorker(completedFetch: CompletedFetch) {
    trafficMan ! AckMessage(completedFetch.originalUrl)
  }

}