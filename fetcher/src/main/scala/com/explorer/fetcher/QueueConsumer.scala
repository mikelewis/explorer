package com.explorer.fetcher
import com.explorer.common.{ BaseQueueConsumer, QueueWithAcking, RedisConfig }
import akka.actor.Actor
import akka.actor.ActorRef
import net.fyrie.redis._
import akka.actor.ActorSystem
import akka.util.ByteString
import net.liftweb.json._
import net.liftweb.json.Serialization.{ read, write }

class QueueConsumer(redisConfig: RedisConfig, queue: String, currentlyProcessingQ: String)
  extends BaseQueueConsumer[WorkType](redisConfig, queue) with QueueWithAcking[WorkType] {

  val currentlyProcessingQueue = currentlyProcessingQ

  override def strToJob(msg: String) = FetchUrl(msg) // msg is url
}