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

  describe("processExceptionFromResponse") {
    it("should return a ConnectionError") {
      val ex = new java.net.ConnectException
      actor.processExceptionFromResponse(ex) should be(ConnectionError(ex))
    }

    it("should return a MalformedUrl") {
      val ex = new java.net.MalformedURLException
      actor.processExceptionFromResponse(ex) should be(MalformedUrl(ex))
    }

    it("should return a UnresolvedAddress") {
      val ex = new java.nio.channels.UnresolvedAddressException
      actor.processExceptionFromResponse(ex) should be(UnresolvedAddress(ex))

    }

    it("should throw an exception that isn't known") {
      evaluating { actor.processExceptionFromResponse(new ArrayIndexOutOfBoundsException) } should produce[ArrayIndexOutOfBoundsException]
    }
  }

  describe("responseToCompletedFetch") {
    it("should return a SuccessfulFetch on a completed request") {
      val promise = actor.processUrl(getUrl("links.html"))
      val response = Await.result(promise.future, 10 seconds).asInstanceOf[Response]
      actor.responseToCompletedFetch(getUrl("links.html"), response) should be(SuccessfulFetch(getUrl("links.html"), response.getUri.toString, response.getStatusCode, actor.getHeadersFromResponse(response), response.getResponseBody))
    }

    it("should return a FailedFetch, AbortedDocumentDuringStatus when aborted during status check") {
      var (actorRef, actor) = testUrlWorker(FetchConfig(hooks = TestStatusHookFail))
      val promise = actor.processUrl(getUrl("links.html"))
      val response = Await.result(promise.future, 10 seconds).asInstanceOf[Response]
      actor.responseToCompletedFetch(getUrl("links.html"), response) should be(FailedFetch(getUrl("links.html"), AbortedDocumentDuringStatus(response.getUri.toString)))
    }

    it("should return a FailedFetch, AbortedDocumentDuringHeaders when aborted during status check") {
      var (actorRef, actor) = testUrlWorker(FetchConfig(hooks = TestHeaderHookFail))
      val promise = actor.processUrl(getUrl("links.html"))
      val response = Await.result(promise.future, 10 seconds).asInstanceOf[Response]
      actor.responseToCompletedFetch(getUrl("links.html"), response) should be(FailedFetch(getUrl("links.html"), AbortedDocumentDuringHeaders(response.getUri.toString)))
    }
  }

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