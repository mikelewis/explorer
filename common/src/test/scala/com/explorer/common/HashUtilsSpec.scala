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
import java.util.zip.CRC32

@RunWith(classOf[JUnitRunner])
class HashUtilsSpec extends MasterSuite with BeforeAndAfterEach {
  describe("crc32 that takes array of bytes") {
    it("should return correct crc32 hash") {
      val c = new CRC32
      c.update(Array(1, 2, 3).map(_.toByte))
      HashUtils.crc32(Array(1, 2, 3).map(_.toByte)) should be(c.getValue)
    }
  }

  describe("crc32 take takes string") {
    it("should return correct crc32 hash") {
      val c = new CRC32
      c.update("hey".getBytes)
      HashUtils.crc32("hey") should be(c.getValue)
    }
  }
}