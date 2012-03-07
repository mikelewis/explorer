package com.explorer.fetcher
import com.explorer.common.TrafficMan
import akka.actor.ActorRef

class FetcherTrafficMan(fetcher: ActorRef, queue: ActorRef)
  extends TrafficMan(fetcher, queue) {
 
  def trafficDispatcher = {
    case job: FetchUrl => fetcher ! job
  }
}