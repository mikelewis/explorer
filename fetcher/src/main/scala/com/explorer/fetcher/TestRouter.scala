package com.explorer.fetcher

import akka.actor.Props
import akka.actor.ActorSystem

object TestRouter extends App {
  val system = ActorSystem("MySystem")
  val router = system.actorOf(Props[PrintlnActor].withRouter(new FetcherRouter(nrOfInstances = 1, resizer = Some(new MyCustomResizer))), "router")
    
  for (num <- (1 to 3000)) {
    router ! FetchUrl(num.toString, "yahoo.com")
  }
}