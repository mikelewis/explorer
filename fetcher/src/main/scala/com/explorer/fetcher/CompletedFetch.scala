package com.explorer.fetcher
import akka.actor.ActorRef

sealed trait FetchTypes
abstract class CompletedFetch(val originalUrl: String) extends FetchTypes
case class SuccessfulFetch(override val originalUrl: String, val newUrl: String, val status: Int, val headers: Map[String, String], val body: String)
  extends CompletedFetch(originalUrl)
case class FailedFetch(override val originalUrl: String, val failedReason: FailedReason)
  extends CompletedFetch(originalUrl)