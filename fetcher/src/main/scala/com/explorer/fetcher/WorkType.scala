package com.explorer.fetcher

sealed trait WorkType

case class FetchUrl(host: String, url: String) extends WorkType
case class DoneFetchUrl(host: String, url: String) extends WorkType
case class DownloadUrl(host: String, url: String) extends WorkType
case class DoneUrlWorker(host: String, url: String) extends WorkType
