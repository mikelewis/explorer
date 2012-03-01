package com.explorer.processor
import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.OneInstancePerTest
import akka.testkit.TestActorRef
import akka.actor.Actor
import akka.actor.ActorSystem
import com.ning.http.client.AsyncHttpClient
import akka.actor.ActorRef
import com.explorer.common.BaseMasterSuite

trait MasterSuite extends BaseMasterSuite {
  implicit val system = ActorSystem("ProcessorSystem")
  
 // SystemSettings.config = Settings(system)
}