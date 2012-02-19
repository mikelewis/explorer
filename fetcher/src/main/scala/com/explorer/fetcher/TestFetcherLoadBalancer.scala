package com.explorer.fetcher

import akka.actor.Props
import akka.actor.ActorSystem

object TestFetcherLoadBalancer extends App {
  val system = ActorSystem("MySystem")
  val fetcherLoadBalancerActor = system.actorOf(Props(new FetcherLoadBalancer(system)))

  fetcherLoadBalancerActor ! FetchUrl("google.com", "google.com/hey1")
  fetcherLoadBalancerActor ! FetchUrl("zahoo.com", "zahoo.com/yo1")
  fetcherLoadBalancerActor ! FetchUrl("google.com", "google.com/hey2")
  fetcherLoadBalancerActor ! FetchUrl("bahoo.com", "bahoo.com/yo2")

  fetcherLoadBalancerActor ! FetchUrl("yahoo.com", "yahoo.com/yo2")
  fetcherLoadBalancerActor ! FetchUrl("zahoo.com", "zahoo.com/yo2")
  fetcherLoadBalancerActor ! FetchUrl("bahoo.com", "bahoo.com/yo3")

  fetcherLoadBalancerActor ! FetchUrl("google.com", "google.com/hey3")

}