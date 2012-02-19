package com.explorer.fetcher
import akka.actor.Actor
import scala.collection.mutable.Queue
import scala.collection.mutable.HashMap
import akka.actor.Props
import akka.routing.RoundRobinRouter
import akka.actor.ActorSystem
import akka.util.duration._
import akka.actor.ActorRef


case class CurrentlyProcessingItem(host: Option[String], url: Option[String]) {
  def isEmpty = {
    host.isEmpty
  }
}

/*
 * This Actor is responsible for a set number of hosts. No other fetcher will process the same host
 * as the parent router uses a ConsistentHash to determine where to send host.
 * Once the request comes down the fetcher, we will than round robin the requests to UrlWorkers.
 * 
 * Need system to schedule tasks
 */
class Fetcher(parent: ActorRef, system: ActorSystem) extends Actor {
  val hostQueues: HashMap[String, Queue[String]] = HashMap.empty[String, Queue[String]]
  var currentlyProcessing: HashMap[String, CurrentlyProcessingItem] = HashMap.empty[String, CurrentlyProcessingItem]
  val urlWorkers = Vector.fill(5)(context.actorOf(Props[PrintlnActor]))
  val router = context.actorOf(Props[PrintlnActor].withRouter(RoundRobinRouter(urlWorkers)))

  def receive = {
    case FetchUrl(host, url) => handleFetchUrl(host, url)
    case DoneUrlWorker(host, url) => handleDoneUrlWorker(host, url)
  }

  // If we are currently processing a url for that host, queue it up for later fetching.
  // Otherwise, process it now.
  def handleFetchUrl(host: String, url: String) {
    if (currentlyProcessing.contains(host)) {
      enqueueUrl(host, url)
    } else {
      setCurrentlyProcessing(host, url)
      router ! FetchUrl(host, url)
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

  def setCurrentlyProcessing(host: String, url: String) {
    currentlyProcessing(host) = CurrentlyProcessingItem(Some(host), Some(url))
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

        system.scheduler.scheduleOnce(3 seconds) {
          router ! FetchUrl(host, dequeuedUrl)
        }
      }
    }
  }

}