package com.explorer.fetcher
import akka.actor.Actor
import akka.actor.ActorRef
import net.fyrie.redis._
import akka.actor.ActorSystem
import net.liftweb.json._
import akka.util.ByteString
import net.liftweb.json._

class QueueConsumer(queue: String, currentlyProcessingQueue: String) extends Actor
  with akka.actor.ActorLogging {
  implicit val formats = net.liftweb.json.DefaultFormats

  val r = RedisClient(SystemSettings.config.redisHost, SystemSettings.config.redisPort)(context.system)

  def receive = {
    case QueueStartListening => listenToQueue(sender)
  }

  def listenToQueue(trafficMan: ActorRef) {
    //r.brpoplpush(queue, currentlyProcessingQueue)
    val result = r.blpop(Seq(queue))
    result.onSuccess {
      case x => {
        x.foreach {
          case (queue, byteString) => processMessage(trafficMan, byteString)
        }
      }
    } onComplete { result =>
      listenToQueue(trafficMan)
    }
  }

  def processMessage(trafficMan: ActorRef, byteString: ByteString) {
    val job = jsonToJob(byteString.utf8String)
    log.info("Sending job: " + job + " to trafficman " + trafficMan)
    trafficMan ! job
  }

  def jsonToJob(msg: String): WorkType = {
    val json = parse(msg)
    json.extract[FetchUrl]
  }
}