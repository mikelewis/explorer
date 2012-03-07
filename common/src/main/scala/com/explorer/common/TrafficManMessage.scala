package com.explorer.common

sealed trait TrafficManMessage
case class StartTrafficMan extends TrafficManMessage
case class RegisterTrafficMan extends TrafficManMessage
case class AckMessage(msg: String) extends TrafficManMessage