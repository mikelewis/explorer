package com.explorer.fetcher
import akka.actor.ActorSystem
import akka.actor.Props
import com.explorer.common.StartTrafficMan
import com.typesafe.config.ConfigFactory
import com.explorer.common.RedisConfig

object FetcherRunner extends App {
  val config = ConfigFactory.load()
  val system = ActorSystem("FetcherSystem")
  val redisConfig = RedisConfig(host = config.getString("redis.host"), port = config.getInt("redis.port"), database = config.getInt("redis.database"))

  val fetcher = system.actorOf(Props(new Fetcher(FetchConfig(redisConfig = redisConfig))))
  val queueConsumer = system.actorOf(Props(new QueueConsumer(redisConfig,
    config.getString("redis.fetcher_queue"),
    config.getString("redis.fetcher_processing_list"))))

  val trafficMan = system.actorOf(Props(new FetcherTrafficMan(fetcher, queueConsumer)))

  trafficMan ! StartTrafficMan
}