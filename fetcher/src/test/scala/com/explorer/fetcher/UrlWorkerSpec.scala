package com.explorer.fetcher
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.EitherValues._
import org.scalatest.BeforeAndAfterEach
import com.explorer.common.RunTestServer

@RunWith(classOf[JUnitRunner])
class UrlWorkerSpec extends RunTestServer with TestHooks
  with BeforeAndAfterEach with MasterSuite {
  var (actorRef, actor) = testUrlWorker()
  /*
  describe("getHeadersFromResponse") {
    it("should return a map of headers") {
      val result = actor.fetchHtml(getUrl("links.html"))
      val parsedDoc = result.left.get
      parsedDoc.response.getHeaders.clear
      parsedDoc.response.getHeaders.put("test", List("value"))
      parsedDoc.response.getHeaders.put("test2", List("value1"))
      parsedDoc.response.getHeaders.put("test3", List("value1", "value2"))
      val mapHeader = GeneralUtils.getHeadersFromResponse(parsedDoc.response)
      mapHeader.size should be(3)
      mapHeader should (contain key ("test") and contain key ("test2"))
      mapHeader("test") should be("value")
      mapHeader("test2") should be("value1")
      mapHeader("test3") should be("value1,value2")
    }
  }*/
}