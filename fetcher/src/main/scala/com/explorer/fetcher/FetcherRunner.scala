package com.explorer.fetcher
import akka.actor.ActorSystem
import akka.actor.Props
import com.explorer.common.StartTrafficMan
import com.typesafe.config.ConfigFactory

object FetcherRunner extends App {
  val config = ConfigFactory.load()
  val system = ActorSystem("FetcherSystem")

  val fetcher = system.actorOf(Props(new Fetcher(FetchConfig())))
  val queueConsumer = system.actorOf(Props(new QueueConsumer(config.getString("redis.host"),
    config.getInt("redis.port"),
    config.getString("redis.fetcher_queue"),
    config.getString("redis.fetcher_processing_list"))))

  val trafficMan = system.actorOf(Props(new FetcherTrafficMan(fetcher, queueConsumer)))

  trafficMan ! StartTrafficMan
}