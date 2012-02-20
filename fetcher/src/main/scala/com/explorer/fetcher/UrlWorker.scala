package com.explorer.fetcher
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
import akka.actor.Actor
import collection.JavaConversions._
import akka.actor.ActorRef
import akka.dispatch.DefaultPromise
import akka.actor.ActorSystem

class UrlWorker(system: ActorSystem, client: AsyncHttpClient, fetchConfig: FetchConfig) extends Actor
  with akka.actor.ActorLogging {
  implicit def dispatcher = system.dispatcher

  val hooks = fetchConfig.hooks

  def receive = {
    case FetchUrl(host, url) =>
      processUrl(sender, host, url)
  }

  def processUrl(sender: ActorRef, host: String, url: String) {
    val promise = new DefaultPromise[Response]
    promise.onSuccess { 
      case response: Response => sender ! responseToCompletedFetch(host, response)
    } onFailure {
      case ex => sender ! FailedFetch(host, url, processExceptionFromResponse(ex))
    }
    
    client.prepareGet(url).execute(generateHttpHandler(promise))
    promise
  }

  def processExceptionFromResponse(e: Throwable): FailedReason = {
    e match {
      case e =>
        e.getCause() match {
          case ce: java.net.ConnectException => ConnectionError(ce)
          case mu: java.net.MalformedURLException => MalformedUrl(mu)
          case ura: java.nio.channels.UnresolvedAddressException => UnresolvedAddress(ura)
          case somethingElse => throw somethingElse
        }
    }
  }

  def responseToCompletedFetch(host: String, response: Response): CompletedFetch = {
    val url = response.getUri.toString
    if (!response.hasResponseHeaders)
      return FailedFetch(host, url, AbortedDocumentDuringStatus())
    if (!response.hasResponseBody)
      return FailedFetch(host, url, AbortedDocumentDuringHeaders())
    SucessfulFetch(host, url, response.getStatusCode, getHeadersFromResponse(response), response.getResponseBody)
  }

  def generateHttpHandler(promise: DefaultPromise[Response]) = {
    new AsyncHandler[Response]() {
      val builder =
        new Response.ResponseBuilder()

      def onThrowable(t: Throwable) {
        log.error(t.getMessage)
        promise.failure(t)
      }

      def onBodyPartReceived(bodyPart: HttpResponseBodyPart) = {
        val newBuilder = builder.accumulate(bodyPart)
        if (hooks.canContinueFromBodyPartReceived(newBuilder.build, bodyPart))
          STATE.CONTINUE
        else
          STATE.ABORT
      }

      def onStatusReceived(responseStatus: HttpResponseStatus) = {
        val newBuilder = builder.accumulate(responseStatus)
        if (hooks.canContinueFromStatusCode(newBuilder.build, responseStatus.getStatusCode()))
          STATE.CONTINUE
        else
          STATE.ABORT
      }

      def onHeadersReceived(headers: HttpResponseHeaders) = {
        val newBuilder = builder.accumulate(headers)
        val resp = newBuilder.build
        if (hooks.canContinueFromHeaders(resp, getHeadersFromResponse(resp)))
          STATE.CONTINUE
        else
          STATE.ABORT
      }

      def onCompleted() = {
        val response = builder.build()
        promise.success(response)
        response
      }
    }
  }

  def getHeadersFromResponse(response: Response): Map[String, String] = {
    val headers = response.getHeaders()
    headers.keySet.foldLeft(Map.empty[String, String]) { (acum, header) =>
      acum + (header -> headers.getJoinedValue(header, ","))
    }
  }
}