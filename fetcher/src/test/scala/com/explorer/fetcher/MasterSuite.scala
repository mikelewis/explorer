package com.explorer.fetcher
import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.OneInstancePerTest
import akka.testkit.TestActorRef
import akka.actor.Actor
import akka.actor.ActorSystem
import com.ning.http.client.AsyncHttpClient
import akka.actor.ActorRef
import com.explorer.common.BaseMasterSuite

trait MasterSuite extends BaseMasterSuite {
  implicit val system = ActorSystem("FetcherSystem")
  
  SystemSettings.config = Settings(system)

  def testUrlWorker(fetchConfig: FetchConfig = FetchConfig()) = {
    val actorRef = TestActorRef(new UrlWorker(system, new AsyncHttpClient(fetchConfig.httpClientConfig), fetchConfig))
    (actorRef, actorRef.underlyingActor)
  }

  def testFetcher(fetchConfig: FetchConfig = FetchConfig()) = {
    val actorRef = TestActorRef(new Fetcher(fetchConfig))
    (actorRef, actorRef.underlyingActor)
  }

  def testQueue(queue: String = "sample_queue", currentlyProcessing: String = "sample_processing") = {
    val actorRef = TestActorRef(new QueueConsumer(queue, currentlyProcessing))
    (actorRef, actorRef.underlyingActor)
  }

  def testTrafficMan(fetcher: ActorRef = testFetcher()._1, queue: ActorRef = testQueue()._1) = {
    val actorRef = TestActorRef(new TrafficMan(fetcher, queue))
    (actorRef, actorRef.underlyingActor)
  }
}