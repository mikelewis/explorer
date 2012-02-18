package com.explorer.fetcher

import akka.routing.RouterConfig
import akka.actor.ActorRef
import akka.dispatch.Dispatchers
import akka.routing.Resizer
import akka.routing.ConsistentHash
import scala.collection.JavaConversions.iterableAsScalaIterable
import akka.routing.RouteeProvider
import akka.actor.Props
import akka.routing.Route
import akka.routing.Destination
import akka.actor.ActorSystem
import akka.actor.Actor
import akka.routing.DefaultResizer


class PrintlnActor extends Actor {
  def receive = {
    case msg =>
      println("Received message '%s' in actor %s".format(msg, self.path.name))
  }
}

object TestRouter extends App {
  val system = ActorSystem("MySystem")
  val router = system.actorOf(Props[PrintlnActor].withRouter(ConsistentHashRouter(nrOfInstances = 1, resizer = Some(new MyCustomResizer))), "router")

  for (num <- (1 to 3000)) {
    router ! FetchUrl(num.toString, "yahoo.com")
    if (num - 3 > 0)
      router ! FetchUrl((num - 3).toString, "google.com")
  }
}