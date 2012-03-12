package com.explorer.processor
import akka.actor.ActorSystem
import akka.actor.Props
import com.explorer.common.StartTrafficMan

object ProcessRunner extends App {
  val system = ActorSystem("ProcessorSystem")
  SystemSettings.config = Settings(system)

  val processor = system.actorOf(Props[Processor])
  val queueConsumer = system.actorOf(Props(new QueueConsumer(SystemSettings.config.redisHost,
    SystemSettings.config.redisPort,
    SystemSettings.config.redisQueue,
    SystemSettings.config.redisCurrentlyProcessingQueue)))

  val trafficMan = system.actorOf(Props(new ProcessorTrafficMan(processor, queueConsumer)))

  trafficMan ! StartTrafficMan
}