package com.explorer.processor
import akka.actor.Actor

class UrlProcessor extends Actor {
  def receive = {
    // temp
    case ProcessUrls(urls) => sender ! DoneProcessUrls(urls.map { (_, 3) })
  }
}