package com.explorer.fetcher
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.EitherValues._
import org.scalatest.BeforeAndAfterEach

@RunWith(classOf[JUnitRunner])
class JsonHelperSpec extends MasterSuite with BeforeAndAfterEach {
  describe("prepareForFetchedUrlQueue") {
    it("should accept SucessfulFetch") {
      val success = SuccessfulFetch(originalUrl = "oldurl.com", newUrl = "newurl.com", status = 200, headers = Map("test" -> "test1", "test2" -> "test3"), body = "MY BODY")
      val json = JsonHelper.prepareForFetchedUrlQueue(success)
      json should be("""{"original_url":"oldurl.com","final_url":"newurl.com","status":200,"headers":{"test":"test1","test2":"test3"},"body":"MY BODY","message_type":"successful_fetch"}""")
    }

    it("should accept FailedFetch") {
      val failure = FailedFetch(originalUrl = "oldurl.com", failedReason = AbortedDocumentDuringHeaders("google.om"))
      val json = JsonHelper.prepareForFetchedUrlQueue(failure)
      json should be("""{"original_url":"oldurl.com","reason":"aborteddocumentduringheaders","message_type":"failed_fetch"}""")

    }
  }
}