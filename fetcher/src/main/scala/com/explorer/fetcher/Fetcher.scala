package com.explorer.fetcher
import akka.actor.Actor
import scala.collection.mutable.Queue
import scala.collection.mutable.HashMap
import akka.actor.Props
import akka.routing.RoundRobinRouter
import akka.actor.ActorSystem
import akka.util.duration._
import akka.actor.ActorRef
import com.ning.http.client.AsyncHttpClient

case class CurrentlyProcessingItem(host: Option[String], url: Option[String])

/*
 * This Actor is responsible for a set number of hosts. No other fetcher will process the same host
 * as the parent router uses a ConsistentHash to determine where to send host.
 * Once the request comes down the fetcher, we will than round robin the requests to UrlWorkers.
 * 
 * Need system to schedule tasks
 */
class Fetcher(parent: ActorRef, system: ActorSystem, fetchConfig: FetchConfig) extends Actor
  with akka.actor.ActorLogging {
  /*
   * google.com =>
   *    google.com/1
   *    google.com/2
   * yahoo.com =>
   * 	yahoo.com/a
   * 	yahoo.com/b
   */
  val hostQueues: HashMap[String, Queue[String]] = HashMap.empty[String, Queue[String]]
  /*
   * google =>
   * 	CurrentlyProcessingItem(google, google.com/1)
   * ..
   */
  var currentlyProcessing: HashMap[String, CurrentlyProcessingItem] = HashMap.empty[String, CurrentlyProcessingItem]

  val httpClient = new AsyncHttpClient(fetchConfig.httpClientConfig)

  val urlWorkers = Vector.fill(5)(context.actorOf(Props(new UrlWorker(system, httpClient, fetchConfig))))
  val router = context.actorOf(Props(new UrlWorker(system, httpClient, fetchConfig)).withRouter(RoundRobinRouter(urlWorkers)))

  override def postStop() {
    httpClient.close
  }

  def receive = {
    case FetchUrl(host, url) => handleFetchUrl(host, url)
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

  // If we are currently processing a url for that host, queue it up for later fetching.
  // Otherwise, process it now.
  def handleFetchUrl(host: String, url: String) {
    if (currentlyProcessing.contains(host)) {
      enqueueUrl(host, url)
    } else {
      setCurrentlyProcessing(host, url)
      processUrl(host, url)
    }
  }

  // A queue for this host may not exist, if it doesn't... create it.
  def enqueueUrl(host: String, url: String) {
    if (hostQueues.contains(host)) {
      hostQueues(host) += url
    } else {
      hostQueues(host) = Queue(url)
    }
  }

  def handleDoneUrlWorker(host: String, url: String) {
    currentlyProcessing.remove(host)
    parent ! DoneFetchUrl(host, url)

    if (hostQueues.contains(host)) {
      if (hostQueues(host).isEmpty) {
        hostQueues.remove(host)
      } else {
        // schedule process of popped url for 3 seconds
        val dequeuedUrl = hostQueues(host).dequeue
        processUrlLater(host, dequeuedUrl)
      }
    }
  }

  def processUrlLater(host: String, url: String) {
    system.scheduler.scheduleOnce(3 seconds) {
      processUrl(host, url)
    }
  }

  def processUrl(host: String, url: String) {
    println("Processing host: " + host + " url " + url + " on Fetcher: " + self.path.name)
    router ! FetchUrl(host, url)

  }

  def setCurrentlyProcessing(host: String, url: String) {
    currentlyProcessing(host) = CurrentlyProcessingItem(Some(host), Some(url))
  }
}