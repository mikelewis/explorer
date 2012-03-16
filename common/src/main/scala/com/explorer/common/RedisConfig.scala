package com.explorer.common

case class RedisConfig(
  val host: String = "localhost",
  val port: Int = 6379,
  val database: Int = 0)