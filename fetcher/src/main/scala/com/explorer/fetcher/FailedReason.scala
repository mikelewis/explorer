package com.explorer.fetcher

sealed trait FailedReason
case class AbortedDocumentDuringStatus extends FailedReason
case class AbortedDocumentDuringHeaders extends FailedReason
case class SystemError(e: Exception) extends FailedReason
case class ConnectionError(e: java.net.ConnectException) extends FailedReason
case class MalformedUrl(e: java.net.MalformedURLException) extends FailedReason