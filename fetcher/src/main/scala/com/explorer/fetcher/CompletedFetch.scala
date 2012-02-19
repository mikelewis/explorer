package com.explorer.fetcher

sealed trait FetchTypes
abstract class CompletedFetch(val host: String, val url: String) extends FetchTypes
case class SucessfulFetch(override val host: String, override val url: String, status: Int, headers: Map[String, String], body: String)
  extends CompletedFetch(host: String, url: String)
case class FailedFetch(override val host: String, override val url: String, failedReason: FailedReason)
  extends CompletedFetch(host: String, url: String)