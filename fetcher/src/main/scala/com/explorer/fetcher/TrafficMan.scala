package com.explorer.fetcher
import akka.actor.Actor
import akka.actor.ActorRef

class TrafficMan(fetcher: ActorRef, queue: ActorRef) extends Actor {
  def receive = {
    case StartTrafficMan => queue ! QueueStartListening
    case job: FetchUrl => fetcher ! job
  }
}