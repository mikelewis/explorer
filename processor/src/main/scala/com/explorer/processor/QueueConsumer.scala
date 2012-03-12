package com.explorer.processor
import com.explorer.common.{ BaseQueueConsumer, QueueWithAcking }
import akka.actor.Actor
import akka.actor.ActorRef
import net.fyrie.redis._
import akka.actor.ActorSystem
import akka.util.ByteString
import net.liftweb.json._
import net.liftweb.json.Serialization.{ read, write }
import com.explorer.common.JsonCompletedFetch
import com.explorer.common.JsonFailedFetch

class QueueConsumer(host: String, port: Int, queue: String, currentlyProcessingQ: String)
  extends BaseQueueConsumer[WorkType](queue, host, port) with QueueWithAcking[WorkType] {

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