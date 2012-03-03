package com.explorer.fetcher
import akka.actor.Actor
import akka.actor.ActorRef
import com.explorer.common.DoneProcessing
import com.explorer.common.QueueStartListening

class TrafficMan(fetcher: ActorRef, queue: ActorRef) extends Actor {
  def receive = {
    case StartTrafficMan => queue ! QueueStartListening
    case job: FetchUrl => fetcher ! job
    case doneJob: DoneFetchUrl => handleDoneFetchUrl(doneJob)
  }

  def handleDoneFetchUrl(doneJob: DoneFetchUrl) {
    queue ! DoneProcessing(doneJob.url)
  }
}