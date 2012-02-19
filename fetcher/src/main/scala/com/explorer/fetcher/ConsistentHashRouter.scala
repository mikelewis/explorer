package com.explorer.fetcher
import akka.actor.ActorRef
import akka.routing.Resizer
import akka.routing.RouterConfig
import akka.dispatch.Dispatchers
import akka.routing.ConsistentHash
import akka.routing.RouteeProvider
import akka.actor.Props
import akka.routing.Destination
import scala.collection.JavaConversions.iterableAsScalaIterable
import akka.routing.Route
import akka.routing.Broadcast

abstract case class ConsistentHashRouter[HashKeyType](nrOfInstances: Int = 0, routees: Iterable[String] = Nil, override val resizer: Option[Resizer] = None,
  val routerDispatcher: String = Dispatchers.DefaultDispatcherId)
  extends RouterConfig {

  val consistentHash = new ConsistentHash[String](routees.toIndexedSeq, 100)

  def this(routeePaths: java.lang.Iterable[String]) = {
    this(routees = iterableAsScalaIterable(routeePaths))
  }

  def this(resizer: Resizer) = this(resizer = Some(resizer))

  def hashKeyFromMessage(message: Any): HashKeyType

  def hashKeyToByteArray(key: HashKeyType): Array[Byte]

  def createRoute(props: Props, routeeProvider: RouteeProvider): Route = {
    routeeProvider.createAndRegisterRoutees(props, nrOfInstances, routees)

    def getActorFromRing(key: HashKeyType) = {
      resizer.foreach { resizer =>
        resizer match {
          case r: MyCustomResizer => {
            if (!r.addActors.isEmpty) {
              r.addActors.foreach(a => consistentHash.+=(a.path.toString()))
              r.resetAddActors
            }

            if (!r.abandonActors.isEmpty) {
              r.abandonActors.foreach(a => consistentHash.-=(a.path.toString()))
              r.resetAbandonActors
            }
          }
          case _ =>
        }
      }

      routeeProvider.context.actorFor(consistentHash.nodeFor(hashKeyToByteArray(key)))
    }

    {
      case (sender, message) =>
        message match {
          case Broadcast(msg) => toAll(sender, routeeProvider.routees)
          case anythingelse => List(Destination(sender, getActorFromRing(hashKeyFromMessage(anythingelse))))
        }
    }
  }
}