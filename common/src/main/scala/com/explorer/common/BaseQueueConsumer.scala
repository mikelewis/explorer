package com.explorer.common
import akka.actor.Actor
import akka.actor.ActorRef
import net.fyrie.redis._
import akka.actor.ActorSystem
import akka.util.ByteString

abstract class BaseQueueConsumer[J](val queue: String, val redisHost: String, val redisPort: Int) extends Actor
  with akka.actor.ActorLogging {
  val r = RedisClient(redisHost, redisPort)(context.system)

  override def postStop {
    r.disconnect
  }

  def receive = {
    case QueueStartListening => listenToQueue(sender)
  }

  def listenToQueue(listener: ActorRef) {
    val result = r.blpop(Seq(queue))
    result.onSuccess {
      case x => {
        x.foreach {
          case (queue, byteString) => processMessage(listener, byteString)
        }
      }
    } onComplete { result =>
      listenToQueue(listener)
    }
  }

  def processMessage(trafficMan: ActorRef, byteString: ByteString) {
    val job = strToJob(byteString.utf8String)
    log.info("Sending job: " + job + " to trafficman " + trafficMan)
    trafficMan ! job
  }

  protected def strToJob(msg: String): J
}