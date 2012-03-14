package com.explorer.processor
import com.explorer.common.TrafficMan
import akka.actor.ActorRef

class ProcessorTrafficMan(processor: ActorRef, queue: ActorRef)
  extends TrafficMan(processor, queue) {
 
  def trafficDispatcher = {
    case processorJob: BaseProcessorWorkType => processor ! processorJob
  }
}