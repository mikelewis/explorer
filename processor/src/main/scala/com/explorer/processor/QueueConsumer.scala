package com.explorer.processor
import akka.actor.Actor
import akka.actor.ActorRef
import net.fyrie.redis._
import akka.actor.ActorSystem
import akka.util.ByteString
import com.explorer.common.{ BaseQueueConsumer, JsonCompletedFetch, JsonFailedFetch, QueueWithAcking, RedisConfig }
import net.liftweb.json._
import net.liftweb.json.Serialization.{ read, write }

class QueueConsumer(redisConfig: RedisConfig, queue: String, currentlyProcessingQ: String)
  extends BaseQueueConsumer[WorkType](redisConfig, queue) with QueueWithAcking[WorkType] {

  val currentlyProcessingQueue = currentlyProcessingQ

  override def strToJob(msg: String) = {
    JsonHelper.jsonToJsonObject(msg) match {
      case JsonCompletedFetch(originalUrl, finalUrl,
        status, headers, body, _) =>
        ProcessCompletedFetchedUrl(msg,
          originalUrl, finalUrl,
          status, headers, body)
      case JsonFailedFetch(originalUrl, reason, _) =>
        ProcessFailedFetchedUrl(msg, originalUrl, reason)
    }
  }
}