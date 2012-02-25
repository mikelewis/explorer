package com.explorer.fetcher

sealed trait FetchTypes
abstract class CompletedFetch(val url: String) extends FetchTypes
case class SucessfulFetch(override val url: String, status: Int, headers: Map[String, String], body: String)
  extends CompletedFetch(url: String)
case class FailedFetch(override val url: String, failedReason: FailedReason)
  extends CompletedFetch(url: String)