package com.explorer.fetcher

sealed trait FailedReason
case class AbortedDocumentDuringStatus(url: String) extends FailedReason
case class AbortedDocumentDuringHeaders(url: String) extends FailedReason
case class SystemError(e: Exception) extends FailedReason
case class ConnectionError(e: java.net.ConnectException) extends FailedReason
case class MalformedUrl(e: java.net.MalformedURLException) extends FailedReason
case class UnresolvedAddress(e: java.nio.channels.UnresolvedAddressException) extends FailedReason