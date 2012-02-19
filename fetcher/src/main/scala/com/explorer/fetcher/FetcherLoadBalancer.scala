package com.explorer.fetcher
import akka.actor.Actor
import akka.actor.Props
import akka.actor.ActorSystem
import akka.routing.Broadcast

class FetcherLoadBalancer(system: ActorSystem, fetchConfig: FetchConfig) extends Actor {

  val fetchers = Vector.fill(5)(context.actorOf(Props(new Fetcher(self, system, fetchConfig))))
  val router = context.actorOf(Props(new Fetcher(self, system, fetchConfig)).withRouter(
    new FetcherRouter(routees = fetchers.map(_.path.toString()), resizer = Some(new MyCustomResizer))))

  def receive = {
    case FetchUrl(host, url) => router ! FetchUrl(host, url) // This is for debugging as we would hook this up to a queue (RabbitMQ for example).
    case DoneFetchUrl(host, url) => handleDoneFetchUrl(host, url)
  }

  // ack the message to the queue (RabbitMQ)
  def handleDoneFetchUrl(host: String, url: String) {
    println("url: " + url + " fetched")
  }
}