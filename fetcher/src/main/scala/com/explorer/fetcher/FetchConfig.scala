package com.explorer.fetcher
import com.ning.http.client.AsyncHttpClientConfig

case class FetchConfig(
  hooks: Hooks = DefaultHooks,
  httpClientConfig: AsyncHttpClientConfig = GeneralUtils.defaultAsyncHttpConfig)