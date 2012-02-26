package com.explorer.fetcher
import akka.actor.Actor
import akka.actor.ActorRef
import net.fyrie.redis._
import akka.actor.ActorSystem
import net.liftweb.json._
import akka.util.ByteString
import net.liftweb.json._
import net.liftweb.json.Serialization.{read,write}

class QueueConsumer(queue: String, currentlyProcessingQueue: String) extends Actor
  with akka.actor.ActorLogging {
  implicit val formats = net.liftweb.json.DefaultFormats

  val r = RedisClient(SystemSettings.config.redisHost, SystemSettings.config.redisPort)(context.system)

  def receive = {
    case QueueStartListening => listenToQueue(sender)
    case DoneFetchUrl(url) => ackUrl(url)
  }

  def listenToQueue(trafficMan: ActorRef) {
    val result = r.brpoplpush(queue, currentlyProcessingQueue)
    result.onSuccess {
      case x => {
        x.foreach {
          byteString => processMessage(trafficMan, byteString)
        }
      }
    } onComplete { result =>
      listenToQueue(trafficMan)
    }
  }
  
  def ackUrl(url: String) {
    val jobToRemove = write(FetchUrl(url))
    log.info("Finished url job " + jobToRemove)
    log.info("Removing from " + currentlyProcessingQueue + " in queue")
    r.quiet.lrem(currentlyProcessingQueue, jobToRemove, 1)
  }

  def processMessage(trafficMan: ActorRef, byteString: ByteString) {
    val job = jsonToJob(byteString.utf8String)
    log.info("Sending job: " + job + " to trafficman " + trafficMan)
    trafficMan ! job
  }

  def jsonToJob(msg: String): FetchUrl = {
    read[FetchUrl](msg)
  }
}