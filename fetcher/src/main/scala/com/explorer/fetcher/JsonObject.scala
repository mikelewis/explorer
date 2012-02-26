package com.explorer.fetcher

trait JsonObject
abstract class BaseJsonObject(message_type: String, original_url: String)
case class JsonCompletedFetch(original_url: String, final_url: String,
  status: Int, headers: Map[String, String], body: String, message_type: String ="success")
  extends BaseJsonObject(message_type, original_url)
case class JsonFailedFetch(original_url: String, reason: String, message_type: String ="failure")
  extends BaseJsonObject(message_type, original_url)