package com.explorer.fetcher
import akka.actor.ActorRef

sealed trait FetchTypes
abstract class CompletedFetch(val originalUrl: String) extends FetchTypes
case class SuccessfulFetch(override val originalUrl: String, newUrl: String, status: Int, headers: Map[String, String], body: String)
  extends CompletedFetch(originalUrl)
case class FailedFetch( override val originalUrl: String, failedReason: FailedReason)
  extends CompletedFetch(originalUrl)