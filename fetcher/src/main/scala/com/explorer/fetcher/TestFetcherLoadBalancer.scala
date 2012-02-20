package com.explorer.fetcher

import akka.actor.Props
import akka.actor.ActorSystem
import com.ning.http.client.Response

object SampleHooks extends Hooks {
  override def canContinueFromStatusCode(response: Response, status: Integer) = {
    if (status == 404)
      false
    else
      true
  }
}

object TestFetcherLoadBalancer extends App {
  val system = ActorSystem("MySystem")
  val fetcherLoadBalancerActor = system.actorOf(Props(new FetcherLoadBalancer(system, FetchConfig(hooks = SampleHooks))))

  /*
  fetcherLoadBalancerActor ! FetchUrl("leafo.net", "http://leafo.net")
  fetcherLoadBalancerActor ! FetchUrl("railscasts.com", "http://railscasts.com/episodes/90-fragment-caching-revised")
  fetcherLoadBalancerActor ! FetchUrl("cnn.com", "http://www.cnn.com/2012/02/19/showbiz/whitney-houston/index.html")
  fetcherLoadBalancerActor ! FetchUrl("www.rubyflow.com", "http://www.rubyflow.com/items/7238-jslint-v8-1-1-released")
  fetcherLoadBalancerActor ! FetchUrl("railscasts.com", "http://railscasts.com/episodes/325-backbone-on-rails-part-2")
  fetcherLoadBalancerActor ! FetchUrl("cnn.com", "http://www.cnn.com/2012/02/19/us/washington-avalanche-deaths/index.html?hpt=hp_t1")
  fetcherLoadBalancerActor ! FetchUrl("leafo.net", "http://leafo.net/lessphp/")
  fetcherLoadBalancerActor ! FetchUrl("www.rubyflow.com", "http://www.rubyflow.com/items/7233-i-just-launched-coffee-taster-an-easy-c")
  fetcherLoadBalancerActor ! FetchUrl("leafo.net", "http://leafo.net/sitegen/")
  fetcherLoadBalancerActor ! FetchUrl("techcrunch.com", "http://techcrunch.com/2012/02/19/solvate-shutting-down/")
*/

  fetcherLoadBalancerActor ! FetchUrl("cnn.com", "http://www.cnn.com/4o4.html")

}