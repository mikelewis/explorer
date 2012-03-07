package com.explorer.common
import akka.actor.Actor
import akka.actor.ActorRef
import net.fyrie.redis._
import akka.actor.ActorSystem
import akka.util.ByteString

abstract class BaseQueueConsumer(val queue: String, redisHost: String, redisPort: Int) extends Actor
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

  protected def processMessage(listener: ActorRef, byteString: ByteString)
}