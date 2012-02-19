package com.explorer.fetcher
import com.ning.http.client.AsyncHttpClientConfig

object GeneralUtils {
  def defaultAsyncHttpConfig = {
    val builder = new AsyncHttpClientConfig.Builder()
    builder.setCompressionEnabled(true)
      .setAllowPoolingConnection(true)
      .setMaximumNumberOfRedirects(5)
      .setRequestTimeoutInMs(30000)
      .setFollowRedirects(true)
      .build()
  }
}