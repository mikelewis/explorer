package com.explorer.fetcher
import akka.actor.ActorRef

sealed trait FetchTypes
abstract class CompletedFetch(val trafficMan: ActorRef, val originalUrl: String) extends FetchTypes
case class SuccessfulFetch(override val trafficMan: ActorRef, override val originalUrl: String, newUrl: String, status: Int, headers: Map[String, String], body: String)
  extends CompletedFetch(trafficMan, originalUrl)
case class FailedFetch(override val trafficMan: ActorRef, override val originalUrl: String, failedReason: FailedReason)
  extends CompletedFetch(trafficMan, originalUrl)