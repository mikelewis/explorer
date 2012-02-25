package com.explorer.fetcher
import akka.actor.Actor
import akka.actor.ActorRef
import net.fyrie.redis._
import akka.actor.ActorSystem
import net.liftweb.json._

class QueueConsumer(fetcher: ActorRef) extends Actor {

  val r = RedisClient(SystemSettings.config.redisHost, SystemSettings.config.redisPort)(context.system)

  def receive = {
    case QueueStartListening(queue) => listenToQueue(queue)
  }

  def listenToQueue(queue: String) {
    val result = r.blpop(Seq(queue))
    result.onSuccess {
      case x => {
        x.foreach {
          case (queue, byteArray) => parseAndProcessItem(byteArray)
        }
      }
    } onComplete { result =>
      listenToQueue(queue)
    }
  }

  def parseAndProcessItem(result: akka.util.ByteString) {
    println("AHHH GOT RESULT FROM QUEUE " + result.utf8String)
  }

  def fetchUrl(host: String, url: String) {
    fetcher ! FetchUrl(host, url)
  }
}