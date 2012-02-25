package com.explorer.fetcher
import akka.actor.Actor
import akka.actor.ActorRef
import net.fyrie.redis._
import akka.actor.ActorSystem
import net.liftweb.json._
import akka.util.ByteString
import net.liftweb.json._

class QueueConsumer(fetcher: ActorRef) extends Actor
  with akka.actor.ActorLogging {
  implicit val formats = net.liftweb.json.DefaultFormats

  val r = RedisClient(SystemSettings.config.redisHost, SystemSettings.config.redisPort)(context.system)

  def receive = {
    case QueueStartListening(queue) => listenToQueue(queue)
  }

  def listenToQueue(queue: String) {
    val result = r.blpop(Seq(queue))
    result.onSuccess {
      case x => {
        x.foreach {
          case (queue, byteString) => processMessage(byteString)
        }
      }
    } onComplete { result =>
      listenToQueue(queue)
    }
  }

  def processMessage(byteString: ByteString) {
    val job = jsonToJob(byteString.utf8String)
    log.info("Got job from queue: " + job)
    fetcher ! job
  }

  def jsonToJob(msg: String): WorkType = {
    val json = parse(msg)
    json.extract[FetchUrl]
  }
}