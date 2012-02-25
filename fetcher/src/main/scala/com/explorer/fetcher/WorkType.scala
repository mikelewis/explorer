package com.explorer.fetcher

sealed trait WorkType

case class FetchUrl(url: String) extends WorkType
case class DoneFetchUrl(url: String) extends WorkType
case class DownloadUrl(url: String) extends WorkType
case class DoneUrlWorker(url: String) extends WorkType
case class QueueStartListening(queue: String)