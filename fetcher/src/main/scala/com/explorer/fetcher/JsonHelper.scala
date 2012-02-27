package com.explorer.fetcher
import net.liftweb.json._
import net.liftweb.json.Serialization.{ read, write }
import com.explorer.common.{BaseJsonObject, JsonCompletedFetch, JsonFailedFetch}

object JsonHelper {
  implicit val formats = net.liftweb.json.DefaultFormats

  def prepareForFetchedUrlQueue(successfulFetch: SuccessfulFetch): String = {
    write(JsonCompletedFetch(successfulFetch.originalUrl, successfulFetch.newUrl,
      successfulFetch.status, successfulFetch.headers, successfulFetch.body))
  }

  def prepareForFetchedUrlQueue(failedFetch: FailedFetch): String = {
    val reasonString = failedFetch.failedReason.getClass.getName.toLowerCase.split('.').last
    write(JsonFailedFetch(failedFetch.originalUrl, reasonString))
  }

  def jsonToJsonObject(json: String): BaseJsonObject = {
    (parse(json) \ "message_type").extract[String] match {
      case "successful_fetch" => read[JsonCompletedFetch](json)
      case "failed_fetch" => read[JsonFailedFetch](json)
      case _ => throw new Exception("Invalid message_type for fetcher")
    }
  }
}