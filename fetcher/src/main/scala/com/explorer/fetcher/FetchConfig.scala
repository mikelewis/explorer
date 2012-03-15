package com.explorer.fetcher
import com.ning.http.client.AsyncHttpClientConfig
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

case class FetchConfig(
  numUrlWorkers: Int = 5,
  hooks: Hooks = DefaultHooks,
  httpClientConfig: AsyncHttpClientConfig = GeneralUtils.defaultAsyncHttpConfig,
  akkaConfig: Config = ConfigFactory.load())