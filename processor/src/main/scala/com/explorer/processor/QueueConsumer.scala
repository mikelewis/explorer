package com.explorer.fetcher
import com.explorer.common.{ BaseQueueConsumer, QueueWithAcking }

import akka.actor.Actor
import akka.actor.ActorRef
import net.fyrie.redis._
import akka.actor.ActorSystem
import akka.util.ByteString

import net.liftweb.json._
import net.liftweb.json.Serialization.{ read, write }

class QueueConsumer(host: String, port: Int, queue: String, currentlyProcessingQ: String) extends BaseQueueConsumer(
  queue,
  host,
  port) with QueueWithAcking {

  implicit val formats = net.liftweb.json.DefaultFormats
  val currentlyProcessingQueue = currentlyProcessingQ

  override def processMessage(trafficMan: ActorRef, byteString: ByteString) {
    val job = jsonToJob(byteString.utf8String)
    log.info("Sending job: " + job + " to trafficman " + trafficMan)
    trafficMan ! job
  }

  def jsonToJob(msg: String) = {

  }
}