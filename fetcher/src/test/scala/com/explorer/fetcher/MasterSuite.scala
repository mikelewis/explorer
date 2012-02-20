package com.explorer.fetcher
import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.OneInstancePerTest
import akka.testkit.TestActorRef
import akka.actor.Actor
import akka.actor.ActorSystem
import com.ning.http.client.AsyncHttpClient

class MasterSuite extends FunSpec with ShouldMatchers {
  implicit val system = ActorSystem("testsystem")

  def testUrlWorker(fetchConfig: FetchConfig = FetchConfig()) = {
    val actorRef = TestActorRef(new UrlWorker(system, new AsyncHttpClient(fetchConfig.httpClientConfig), fetchConfig))
    (actorRef, actorRef.underlyingActor)
  }
}