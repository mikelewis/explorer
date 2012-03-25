package com.explorer.fetcher
import com.typesafe.config.ConfigFactory
import akka.testkit.TestActorRef
import akka.actor.Actor
import akka.actor.ActorSystem
import com.ning.http.client.AsyncHttpClient
import akka.actor.ActorRef
import com.explorer.common.BaseMasterSuite
import akka.actor.Props
import com.explorer.common.RedisConfig
import com.explorer.common.HttpRequestor

trait MasterSuite extends BaseMasterSuite {
  val config = ConfigFactory.load()
  implicit val system = ActorSystem("FetcherSystem")
  val redisConfig = RedisConfig(host = config.getString("redis.host"), port = config.getInt("redis.port"), database = config.getInt("redis.database"))
  val defaultFetchConfig = FetchConfig(redisConfig = redisConfig)

  def asyncHttpClient(fetchConfig: FetchConfig = defaultFetchConfig) = {
    new AsyncHttpClient(fetchConfig.httpClientConfig)
  }

  def testUrlWorker(fetchConfig: FetchConfig = defaultFetchConfig) = {
    val httpClient = new AsyncHttpClient(fetchConfig.httpClientConfig)
    val httpRequestor = new HttpRequestor(httpClient, fetchConfig.hooks)

    val actorRef = TestActorRef(new UrlWorker(httpRequestor))
    (actorRef, actorRef.underlyingActor)
  }

  def testActualUrlWorker(fetchConfig: FetchConfig = defaultFetchConfig) = {
    val httpClient = new AsyncHttpClient(fetchConfig.httpClientConfig)
    val httpRequestor = new HttpRequestor(httpClient, fetchConfig.hooks)
    system.actorOf(Props(new UrlWorker(httpRequestor)))
  }
}