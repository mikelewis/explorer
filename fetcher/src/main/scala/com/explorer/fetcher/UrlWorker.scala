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
    case FetchUrl(url) =>
      processUrl(sender, url)
  }

  def processUrl(sender: ActorRef, url: String) {
    log.info("Fetching " + url)
    val promise = new DefaultPromise[Response]
    promise.onSuccess { 
      case response: Response => sender ! responseToCompletedFetch(response)
    } onFailure {
      case ex => sender ! FailedFetch(url, processExceptionFromResponse(ex))
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

  def responseToCompletedFetch(response: Response): CompletedFetch = {
    val url = response.getUri.toString
    if (!response.hasResponseHeaders)
      return FailedFetch(url, AbortedDocumentDuringStatus())
    if (!response.hasResponseBody)
      return FailedFetch(url, AbortedDocumentDuringHeaders())
    SucessfulFetch(url, response.getStatusCode, getHeadersFromResponse(response), response.getResponseBody)
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