package com.explorer.processor
import akka.actor.ActorSystem
import akka.actor.Props
import com.explorer.common.StartTrafficMan
import com.typesafe.config.ConfigFactory
import com.explorer.common.RedisConfig

object ProcessRunner extends App {
  val config = ConfigFactory.load()
  val system = ActorSystem("ProcessorSystem")

  val redisConfig = RedisConfig(host = config.getString("redis.host"), port = config.getInt("redis.port"), database = config.getInt("redis.database"))
  val processorConfig = ProcessorConfig(redisConfig = redisConfig)
  val processor = system.actorOf(Props(new Processor(processorConfig)))

  val queueConsumer = system.actorOf(Props(new QueueConsumer(redisConfig,
    config.getString("redis.processor_queue"),
    config.getString("redis.processor_processing_list"))))

  val trafficMan = system.actorOf(Props(new ProcessorTrafficMan(processor, queueConsumer)))

  trafficMan ! StartTrafficMan
}