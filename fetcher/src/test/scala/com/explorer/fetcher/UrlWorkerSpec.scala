package com.explorer.fetcher
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.EitherValues._
import org.scalatest.BeforeAndAfterEach
import com.explorer.common.RunTestServer
import com.ning.http.client.Response
import akka.util.duration._
import akka.dispatch.Await
import scala.collection.JavaConversions._

@RunWith(classOf[JUnitRunner])
class UrlWorkerSpec extends RunTestServer with TestHooks
  with BeforeAndAfterEach with MasterSuite {
  var (actorRef, actor) = testUrlWorker()

  describe("getHeadersFromResponse") {
    it("should return a map of headers") {
      val promise = actor.processUrl(getUrl("links.html"))
      val response = Await.result(promise.future, 10 seconds).asInstanceOf[Response]
      response.getHeaders.clear
      response.getHeaders.put("test", List("value"))
      response.getHeaders.put("test2", List("value1"))
      response.getHeaders.put("test3", List("value1", "value2"))
      val mapHeader = actor.getHeadersFromResponse(response)
      mapHeader.size should be(3)
      mapHeader should (contain key ("test") and contain key ("test2"))
      mapHeader("test") should be("value")
      mapHeader("test2") should be("value1")
      mapHeader("test3") should be("value1,value2")
    }
  }
}