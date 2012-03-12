package com.explorer.processor
import akka.actor.Extension
import akka.actor.ExtensionId
import akka.actor.ExtensionIdProvider
import akka.actor.ActorSystemImpl
import akka.util.Duration
import com.typesafe.config.Config
import java.util.concurrent.TimeUnit

class SettingsImpl(config: Config) extends Extension {
  val redisHost = config.getString("redis.host")
  val redisPort = config.getInt("redis.port")
  val redisQueue = config.getString("redis.processor_queue")
  val redisCurrentlyProcessingQueue = config.getString("redis.processor_processing_list")
}

object Settings extends ExtensionId[SettingsImpl] with ExtensionIdProvider {

  override def lookup = Settings

  override def createExtension(system: ActorSystemImpl) = new SettingsImpl(system.settings.config)
}

object SystemSettings {
  var config: SettingsImpl = _
}