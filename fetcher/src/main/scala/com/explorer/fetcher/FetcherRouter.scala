package com.explorer.fetcher
import akka.routing.Resizer
import akka.dispatch.Dispatchers

class FetcherRouter(nrOfInstances: Int = 0, routees: Iterable[String] = Nil, override val resizer: Option[Resizer] = None,
  override val routerDispatcher: String = Dispatchers.DefaultDispatcherId)
  extends ConsistentHashRouter(nrOfInstances, routees, resizer, routerDispatcher) {
  type HashKeyType = String

  def hashKeyFromMessage(message: Any): String = {
    message match {
      case FetchUrl(host, _) => host
    }
  }

  def hashKeyToByteArray(key: String): Array[Byte] = {
    key.toCharArray().map(_.toByte)
  }
}