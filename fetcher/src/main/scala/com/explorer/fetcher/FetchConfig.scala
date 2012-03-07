package com.explorer.fetcher
import com.ning.http.client.AsyncHttpClientConfig

case class FetchConfig(
  numUrlWorkers: Int = 5,
  hooks: Hooks = DefaultHooks,
  httpClientConfig: AsyncHttpClientConfig = GeneralUtils.defaultAsyncHttpConfig)