package com.explorer.processor
import akka.actor.Actor
import akka.dispatch.DefaultPromise
import akka.pattern.pipeTo
import com.explorer.common.RedisConfig
import net.fyrie.redis.RedisClient
import com.explorer.common.RedisUtils
import com.explorer.common.HashUtils
import akka.dispatch.Future

class UrlProcessor(redisConfig: RedisConfig) extends Actor with UrlTests {
  implicit def dispatcher = context.dispatcher
  val redisClient = RedisUtils.createRedisWithRedisConfig(redisConfig, context.system)

  def receive = {
    case ProcessUrl(url) =>
      if (!processUrlWithBasic(url))
        sender ! DoNotProceedWithUrl(url)
      else
        processUrlWithFutures(url).pipeTo(sender)
  }

  def processUrlWithBasic(url: String) = !basicUrlTests.find(!_(url)).isDefined

  def processUrlWithFutures(url: String) = {
    var testFutures = futureUrlTests.map(_(url))
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
}