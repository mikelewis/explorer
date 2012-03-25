package com.explorer.common
import collection.JavaConversions._
import com.ning.http.client.Response

object HttpUtils {
  def getHeadersFromResponse(response: Response): Map[String, String] = {
    val headers = response.getHeaders()
    headers.keySet.foldLeft(Map.empty[String, String]) { (acum, header) =>
      acum + (header -> headers.getJoinedValue(header, ","))
    }
  }
}