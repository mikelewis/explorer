package com.explorer.fetcher
import akka.actor.Actor

class PrintlnActor extends Actor {
  def receive = {
    case FetchUrl(host, url) =>
      println("working for " + host + "   " + url)
      Thread.sleep(1500) // pretend to work
      println("Received message '%s' in actor %s".format("Host: " + host + " Url: " + url, self.path.name))
      sender ! DoneFetchUrl(host, url)
  }
}