package com.explorer.fetcher
import akka.routing.DefaultResizer
import akka.actor.ActorRef
import akka.routing.RouteeProvider
import akka.actor.Props

class MyCustomResizer extends DefaultResizer {
  var abandonActors = emptySeqActorRef
  var addActors = emptySeqActorRef

  def emptySeqActorRef = {
    IndexedSeq.empty[ActorRef]
  }

  def resetAddActors {
    addActors = emptySeqActorRef
  }

  def resetAbandonActors {
    abandonActors = emptySeqActorRef
  }

  override def resize(props: Props, routeeProvider: RouteeProvider) {
    val currentRoutees = routeeProvider.routees
    val requestedCapacity = capacity(currentRoutees)

    if (requestedCapacity > 0) {
      val newRoutees = routeeProvider.createRoutees(props, requestedCapacity, Nil)
      addActors = newRoutees
      routeeProvider.registerRoutees(newRoutees)
    } else if (requestedCapacity < 0) {
      val (keep, abandon) = currentRoutees.splitAt(currentRoutees.length + requestedCapacity)
      abandonActors = abandon
      routeeProvider.unregisterRoutees(abandon)
      delayedStop(routeeProvider.context.system.scheduler, abandon)
    }
  }
}