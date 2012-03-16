package com.explorer.common
import net.fyrie.redis._
import akka.actor.ActorSystem

object RedisUtil {
  def createRedisWithRedisConfig(redisConfig: RedisConfig, system: ActorSystem) = {
    val r = RedisClient(redisConfig.host, redisConfig.port)(system)
    r.sync.select(redisConfig.database)
    r
  }
}