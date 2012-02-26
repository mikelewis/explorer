package com.explorer.fetcher
import net.liftweb.json._
import net.liftweb.json.Serialization.{ read, write }

object JsonHelper {
  implicit val formats = net.liftweb.json.DefaultFormats

  def prepareForFetchedUrlQueue(successfulFetch: SucessfulFetch): String = {
    write(JsonCompletedFetch(successfulFetch.originalUrl, successfulFetch.newUrl,
      successfulFetch.status, successfulFetch.headers, successfulFetch.body))
  }

  def prepareForFetchedUrlQueue(failedFetch: FailedFetch): String = {
    val reasonString = failedFetch.failedReason.getClass.getName.toLowerCase
    write(JsonFailedFetch(failedFetch.originalUrl, reasonString))
  }
}