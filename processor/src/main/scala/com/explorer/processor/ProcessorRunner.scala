package com.explorer.processor
import akka.actor.ActorSystem
import akka.actor.Props
import com.explorer.common.StartTrafficMan
import com.typesafe.config.ConfigFactory

object ProcessRunner extends App {
  val config = ConfigFactory.load()
  val system = ActorSystem("ProcessorSystem")

  val processor = system.actorOf(Props[Processor])
  val queueConsumer = system.actorOf(Props(new QueueConsumer(config.getString("redis.host"),
    config.getInt("redis.port"),
    config.getString("redis.processor_queue"),
    config.getString("redis.processor_processing_list"))))

  val trafficMan = system.actorOf(Props(new ProcessorTrafficMan(processor, queueConsumer)))

  trafficMan ! StartTrafficMan
}