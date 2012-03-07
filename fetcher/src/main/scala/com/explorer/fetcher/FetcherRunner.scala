package com.explorer.fetcher
import akka.actor.ActorSystem
import akka.actor.Props
import com.explorer.common.StartTrafficMan

object FetcherRunner extends App {
  val system = ActorSystem("FetcherSystem")
  SystemSettings.config = Settings(system)

  val fetcher = system.actorOf(Props(new Fetcher(FetchConfig())))
  val queueConsumer = system.actorOf(Props(new QueueConsumer(SystemSettings.config.redisHost,
    SystemSettings.config.redisPort,
    SystemSettings.config.redisQueue,
    SystemSettings.config.redisCurrentlyProcessingQueue)))

  val trafficMan = system.actorOf(Props(new FetcherTrafficMan(fetcher, queueConsumer)))

  trafficMan ! StartTrafficMan
}