package com.explorer.fetcher
import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorSystem
import com.ning.http.client.AsyncHttpClient
import akka.routing.RoundRobinRouter
import net.fyrie.redis._

class Fetcher(fetchConfig: FetchConfig) extends Actor
  with akka.actor.ActorLogging {
  val httpClient = new AsyncHttpClient(fetchConfig.httpClientConfig)

  val urlWorkers = Vector.fill(5)(context.actorOf(Props(new UrlWorker(context.system, httpClient, fetchConfig))))
  val router = context.actorOf(Props(new UrlWorker(context.system, httpClient, fetchConfig)).withRouter(RoundRobinRouter(urlWorkers)))

  val r = RedisClient(SystemSettings.config.redisHost, SystemSettings.config.redisPort)(context.system)

  override def postStop() {
    httpClient.close
    r.disconnect
  }

  def receive = {
    case FetchUrl(url) => router ! DownloadUrl(sender, url)
    case completedFetch: CompletedFetch => handleCompletedFetch(completedFetch)
  }

  def handleCompletedFetch(completedFetch: CompletedFetch) {
    completedFetch match {
      case success: SucessfulFetch => handleSuccessFetch(success)
      case failure: FailedFetch => handleFailedFetch(failure)
    }

    handleDoneUrlWorker(completedFetch)
  }

  def handleSuccessFetch(success: SucessfulFetch) {
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
    completedFetch.trafficMan ! DoneFetchUrl(completedFetch.originalUrl)
  }

}