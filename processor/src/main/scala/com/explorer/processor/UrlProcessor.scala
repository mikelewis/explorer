package com.explorer.processor
import akka.actor.Actor
import akka.dispatch.DefaultPromise
import akka.pattern.pipeTo
import com.explorer.common.RedisConfig
import net.fyrie.redis.RedisClient
import com.explorer.common.RedisUtils
import com.explorer.common.HashUtils
import akka.dispatch.Future

class UrlProcessor(redisConfig: RedisConfig) extends Actor {
  implicit def dispatcher = context.dispatcher
  val redisClient = RedisUtils.createRedisWithRedisConfig(redisConfig, context.system)
  val urlTests = List(testValidHost _, testHasSeenUrl _)

  def receive = {
    case ProcessUrl(url) => processUrl(url).pipeTo(sender)
  }

  def processUrl(url: String) = {
    var testFutures = urlTests.map(_(url))
    Future.find(testFutures)(!_).flatMap {
      // case None means that it couldn't find a test that returned false, this is good!
      case None =>
        robotsCrawlDelay(url).map {
          case Some(delay) => ProceedWithUrl(url, delay)
          case None => DoNotProceedWithUrl(url) // When robotsCrawlDelay returns none, means that we can't crawl this url
        }
      case _ => Future { DoNotProceedWithUrl(url) } // wrap in future to match type of case None. CODE SMELL :(
    }
  }

  def robotsCrawlDelay(url: String): Future[Option[Int]] = {
    Future { Some(3) }
  }

  def testValidHost(url: String) = {
    Future { true }
  }

  def testHasSeenUrl(url: String) = {
    val crc32 = HashUtils.crc32(url)
    // change hardcoded namespace
    val redisKey = "processor:seen_url:" + crc32
    redisClient.setnx(redisKey, 1).map {
      // change hard coded expire time (1 hour)
      case true => redisClient.expire(redisKey, 3600); true // Was not set before. Set expire
      case false => false // We've seen before, don't process again
    }
  }
}