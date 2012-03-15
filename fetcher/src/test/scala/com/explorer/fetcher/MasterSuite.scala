package com.explorer.fetcher
import org.scalatest.FunSpec
import com.typesafe.config.ConfigFactory
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.OneInstancePerTest
import akka.testkit.TestActorRef
import akka.actor.Actor
import akka.actor.ActorSystem
import com.ning.http.client.AsyncHttpClient
import akka.actor.ActorRef
import com.explorer.common.BaseMasterSuite
import akka.actor.Props

trait MasterSuite extends BaseMasterSuite {
  val config = ConfigFactory.load()
  implicit val system = ActorSystem("FetcherSystem")

  def asyncHttpClient(fetchConfig: FetchConfig = FetchConfig()) = {
    new AsyncHttpClient(fetchConfig.httpClientConfig)
  }

  def testUrlWorker(fetchConfig: FetchConfig = FetchConfig()) = {
    val actorRef = TestActorRef(new UrlWorker(asyncHttpClient(fetchConfig), fetchConfig))
    (actorRef, actorRef.underlyingActor)
  }

  def testFetcher(fetchConfig: FetchConfig = FetchConfig()) = {
    val actorRef = TestActorRef(new Fetcher(fetchConfig))
    (actorRef, actorRef.underlyingActor)
  }

  def testQueue(queue: String = "sample_queue", currentlyProcessing: String = "sample_processing") = {
    val queueConsumerObj = new QueueConsumer(config.getString("redis.host"),
    config.getInt("redis.port"),
    config.getString("redis.fetcher_queue"),
    config.getString("redis.fetcher_processing_list"))
    val actorRef = TestActorRef(queueConsumerObj)
    (actorRef, actorRef.underlyingActor)
  }

  def testTrafficMan(fetcher: ActorRef = testFetcher()._1, queue: ActorRef = testQueue()._1) = {
    val actorRef = TestActorRef(new FetcherTrafficMan(fetcher, queue))
    (actorRef, actorRef.underlyingActor)
  }

  def testActualUrlWorker(fetchConfig: FetchConfig = FetchConfig()) = {
    system.actorOf(Props(new UrlWorker(asyncHttpClient(fetchConfig), fetchConfig)))
  }
}