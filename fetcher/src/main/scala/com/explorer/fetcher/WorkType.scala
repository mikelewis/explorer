package com.explorer.fetcher
import akka.actor.ActorRef

sealed trait WorkType

case class FetchUrl(url: String) extends WorkType
case class DoneFetchUrl(url: String) extends WorkType
case class DownloadUrl(url: String) extends WorkType
case class DoneUrlWorker(url: String) extends WorkType
case class StartTrafficMan extends WorkType
case class RegisterTrafficMan extends WorkType