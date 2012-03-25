package com.explorer.fetcher

import com.explorer.common.HttpRequestor
import com.explorer.common.HttpUtils

import akka.actor.Actor
import akka.actor.ActorRef
import akka.dispatch.DefaultPromise
import akka.pattern.pipeTo
import collection.JavaConversions._
import com.ning.http.client.AsyncHandler.STATE
import com.ning.http.client.AsyncHandler
import com.ning.http.client.AsyncHttpClient
import com.ning.http.client.HttpResponseBodyPart
import com.ning.http.client.HttpResponseHeaders
import com.ning.http.client.HttpResponseStatus
import com.ning.http.client.Response
import com.ning.http.client.AsyncHttpClientConfig
import com.ning.http.client.AsyncHttpClientConfig.Builder
import java.net.ConnectException

class UrlWorker(httpRequestor: HttpRequestor) extends Actor
  with akka.actor.ActorLogging {
  implicit def dispatcher = context.dispatcher

  def receive = {
    case DownloadUrl(url) =>
      createHttpRequest(url).map { result =>
        result match {
          case response: Response => responseToCompletedFetch(url, response)
        }
      }.recover {
        case ex => FailedFetch(url, processExceptionFromResponse(ex))
      }.pipeTo(sender)
  }

  def createHttpRequest(url: String) = {
    log.info("Creating http request for url: " + url)
    httpRequestor.processUrl(url, new DefaultPromise[Response])
  }

  def processExceptionFromResponse: PartialFunction[Throwable, FailedReason] = {
    case ce: java.net.ConnectException => ConnectionError(ce)
    case mu: java.net.MalformedURLException => MalformedUrl(mu)
    case ura: java.nio.channels.UnresolvedAddressException => UnresolvedAddress(ura)
    case somethingElse => throw somethingElse
  }

  def responseToCompletedFetch(originalUrl: String, response: Response): CompletedFetch = {
    if (!response.hasResponseHeaders)
      return FailedFetch(originalUrl, AbortedDocumentDuringStatus(response.getUri.toString))
    if (!response.hasResponseBody)
      return FailedFetch(originalUrl, AbortedDocumentDuringHeaders(response.getUri.toString))
    SuccessfulFetch(originalUrl, response.getUri.toString, response.getStatusCode, HttpUtils.getHeadersFromResponse(response), response.getResponseBody)
  }
}