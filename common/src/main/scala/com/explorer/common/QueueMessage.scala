package com.explorer.common

sealed trait QueueMessage
case class QueueStartListening extends QueueMessage