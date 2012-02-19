package com.explorer.fetcher

import akka.actor.Props
import akka.actor.ActorSystem

object TestFetcherLoadBalancer extends App {
  val system = ActorSystem("MySystem")
  val fetcherLoadBalancerActor = system.actorOf(Props(new FetcherLoadBalancer(system, FetchConfig())))

  fetcherLoadBalancerActor ! FetchUrl("leafo.net", "http://leafo.net")
  fetcherLoadBalancerActor ! FetchUrl("leafo.net", "http://leafo.net/lessphp/")
  fetcherLoadBalancerActor ! FetchUrl("leafo.net", "http://leafo.net/sitegen/")

}