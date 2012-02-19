package com.explorer.fetcher
import akka.actor.Actor

class PrintlnActor extends Actor {
  def receive = {
    case FetchUrl(host, url) =>
      println("working for " + host + "   " + url + " on actor " + self.path.name)
      Thread.sleep(1500) // pretend to work
      sender ! DoneUrlWorker(host, url)
  }
}