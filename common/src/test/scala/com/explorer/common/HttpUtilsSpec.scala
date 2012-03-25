package com.explorer.common
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.EitherValues._
import org.scalatest.BeforeAndAfterEach
import akka.dispatch.Await
import com.ning.http.client.Response
import com.ning.http.client.providers.netty._
import com.ning.http.client.AsyncHttpClient
import scala.collection.JavaConversions._

@RunWith(classOf[JUnitRunner])
class HttpUtilsSpec extends MasterSuite with BeforeAndAfterEach {
  // Move this to common.HttpUtls
  describe("getHeadersFromResponse") {
    it("should return a map of headers") {
      val client = new AsyncHttpClient();
      val response = client.prepareGet("http://google.com").execute().get();

      response.getHeaders.clear
      response.getHeaders.put("test", List("value"))
      response.getHeaders.put("test2", List("value1"))
      response.getHeaders.put("test3", List("value1", "value2"))
      val mapHeader = HttpUtils.getHeadersFromResponse(response)
      mapHeader.size should be(3)
      mapHeader should (contain key ("test") and contain key ("test2"))
      mapHeader("test") should be("value")
      mapHeader("test2") should be("value1")
      mapHeader("test3") should be("value1,value2")
    }
  }
}