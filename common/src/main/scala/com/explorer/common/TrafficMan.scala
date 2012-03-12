package com.explorer.common
import akka.actor.Actor
import akka.actor.ActorRef

abstract class TrafficMan(other: ActorRef, queue: ActorRef) extends Actor {
  def receive = {
    trafficDispatcher orElse baseDispatcher
  }

  def baseDispatcher: PartialFunction[Any, Unit] = {
    case StartTrafficMan => startTrafficMan
    case AckMessage(msg) => queue ! DoneProcessing(msg)
  }

  protected def trafficDispatcher: PartialFunction[Any, Unit]

  def startTrafficMan {
    other ! RegisterTrafficMan
    queue ! QueueStartListening
  }
}