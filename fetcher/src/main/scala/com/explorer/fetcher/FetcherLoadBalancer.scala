package com.explorer.fetcher
import akka.actor.Actor
import scala.collection.mutable.Queue
import scala.collection.mutable.HashMap
import akka.actor.Props
import akka.actor.ActorSystem
import akka.util.duration._

case class CurrentlyProcessingItem(host: Option[String], url: Option[String]) {
  def isEmpty = {
    host.isEmpty
  }
}

class FetcherLoadBalancer(system: ActorSystem) extends Actor {
  val hostQueues: HashMap[String, Queue[String]] = HashMap.empty[String, Queue[String]]
  var currentlyProcessing: HashMap[String, CurrentlyProcessingItem] = HashMap.empty[String, CurrentlyProcessingItem]
  val urlWorkers = Vector.fill(5)(context.actorOf(Props[PrintlnActor]))

  val router = context.actorOf(Props[PrintlnActor].withRouter(new FetcherRouter(routees = urlWorkers.map(_.path.toString()), resizer = Some(new MyCustomResizer))))

  def receive = {
    case FetchUrl(host, url) => handleFetchUrl(host, url)
    case DoneFetchUrl(host, url) => handleDoneUrl(host, url)
  }

  /*
   * If we are currently processing a url for a given host, add to the queue and process it later,
   * otherwise process it now.
   */
  def handleFetchUrl(host: String, url: String) {
    if (currentlyProcessing.contains(host)) {
      enqueueUrl(host, url)
    } else {
      setCurrentlyProcessing(host, url)
      router ! FetchUrl(host, url)
    }
  }

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

  def handleDoneUrl(host: String, url: String) {
    currentlyProcessing.remove(host)
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

    // debug
    if (hostQueues.isEmpty) {
      println("SHUTTING DOWN")
      system.shutdown
    }
  }
}