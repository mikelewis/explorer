package com.explorer.processor
import net.liftweb.json.parse
import net.liftweb.json.Serialization.{ read, write }
import com.explorer.common.{BaseJsonObject, JsonCompletedFetch, JsonFailedFetch}

object JsonHelper {
  implicit val formats = net.liftweb.json.DefaultFormats

  def jsonToJsonObject(json: String): BaseJsonObject = {
    (parse(json) \ "message_type").extract[String] match {
      case "successful_fetch" => read[JsonCompletedFetch](json)
      case "failed_fetch" => read[JsonFailedFetch](json)
      case _ => throw new Exception("Invalid message_type for fetcher")
    }
  }
}