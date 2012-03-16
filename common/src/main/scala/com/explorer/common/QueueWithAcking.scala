package com.explorer.common
import akka.actor.ActorRef
import net.fyrie.redis.RedisClient

case class DoneProcessing(msg: String)

trait QueueWithAcking[J] extends BaseQueueConsumer[J] {
  val ackingClient = RedisUtil.createRedisWithRedisConfig(redisConfig, context.system)

  abstract override def postStop {
    super.postStop
    ackingClient.disconnect
  }

  abstract override def receive = {
    ({
      case DoneProcessing(msg) => ackMsg(msg)
    }: PartialFunction[Any, Unit]) orElse super.receive
  }

  override def listenToQueue(listener: ActorRef) {
    val result = r.brpoplpush(queue, currentlyProcessingQueue)
    result.onSuccess {
      case x => {
        x.foreach {
          byteString => processMessage(listener, byteString)
        }
      }
    } onComplete { result =>
      listenToQueue(listener)
    }
  }

  def ackMsg(msg: String) {
    log.info("Removing " + msg + " from " + currentlyProcessingQueue)
    ackingClient.quiet.lrem(currentlyProcessingQueue, msg, 1)

  }

  def currentlyProcessingQueue: String
}