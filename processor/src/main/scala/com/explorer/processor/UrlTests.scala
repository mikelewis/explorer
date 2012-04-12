package com.explorer.processor
import akka.dispatch.Future
import com.explorer.common.HashUtils
import akka.dispatch.MessageDispatcher
import net.fyrie.redis.RedisClient

trait UrlTests {
  implicit def dispatcher: MessageDispatcher
  val redisClient: RedisClient
  val basicUrlTests = List(validUrl _)
  val futureUrlTests = List(testValidHost _, testHasSeenUrl _)

  /*
   * Basic Tests
   */
  def validUrl(url: String) = true // stub

  /*
   * Test that return futures
   */
  def robotsCrawlDelay(url: String): Future[Option[Int]] = Future { Some(3) } //stub

  def testValidHost(url: String) = Future { true } // stub

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