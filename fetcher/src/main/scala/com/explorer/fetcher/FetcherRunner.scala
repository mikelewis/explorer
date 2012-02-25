package com.explorer.fetcher
import akka.actor.ActorSystem
import akka.actor.Props

object FetcherRunner extends App {
  val system = ActorSystem("FetcherSystem")
  SystemSettings.config = Settings(system)

  val fetcher = system.actorOf(Props(new Fetcher(FetchConfig())))
  
  val queueConsumer = system.actorOf(Props(new QueueConsumer(fetcher)))
  queueConsumer ! QueueStartListening(SystemSettings.config.redisQueue)
}