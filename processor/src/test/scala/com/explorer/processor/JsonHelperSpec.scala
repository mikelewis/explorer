package com.explorer.processor
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.EitherValues._
import org.scalatest.BeforeAndAfterEach
import com.explorer.common.{ BaseJsonObject, JsonCompletedFetch, JsonFailedFetch }

@RunWith(classOf[JUnitRunner])
class JsonHelperSpec extends MasterSuite {
  describe("jsonToJsonObject") {
    it("should parse a proper JsonCompletedFetch") {
      //case class JsonCompletedFetch(original_url: String, final_url: String,
      //status: Int, headers: Map[String, String], body: String, message_type: String = "successful_fetch")
      val json = """{"original_url":"oldurl.com","final_url":"newurl.com","status":200,"headers":{"test":"test1","test2":"test3"},"body":"MY BODY","message_type":"successful_fetch"}"""
      JsonHelper.jsonToJsonObject(json) should be(JsonCompletedFetch(original_url = "oldurl.com", final_url = "newurl.com", status = 200, headers = Map("test" -> "test1", "test2" -> "test3"), body = "MY BODY"))
    }

    it("should parse a proper JsonFailedFetch") {
      val json = """{"original_url":"oldurl.com","reason":"aborteddocumentduringheaders","message_type":"failed_fetch"}"""
      JsonHelper.jsonToJsonObject(json) should be(JsonFailedFetch(original_url="oldurl.com", reason="aborteddocumentduringheaders"))
    }

    it("should blow up with an invalid message_type") {
      val json = """{"original_url":"oldurl.com","reason":"aborteddocumentduringheaders","message_type":"babies"}"""
      evaluating { JsonHelper.jsonToJsonObject(json) } should produce[Exception]
    }
  }
}