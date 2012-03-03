package com.explorer.fetcher
import akka.actor.ActorRef

sealed trait WorkType

case class FetchUrl(url: String) extends WorkType
case class DoneFetchUrl(url: String) extends WorkType
case class DownloadUrl(listener: ActorRef, url: String) extends WorkType
case class DoneUrlWorker(url: String) extends WorkType
case class StartTrafficMan extends WorkType