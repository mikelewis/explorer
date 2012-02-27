package com.explorer.common

trait JsonObject
abstract class BaseJsonObject(message_type: String)
abstract class BaseFetchJsonObject(message_type: String, original_url: String) extends BaseJsonObject(message_type)
case class JsonCompletedFetch(original_url: String, final_url: String,
  status: Int, headers: Map[String, String], body: String, message_type: String = "successful_fetch")
  extends BaseFetchJsonObject(message_type, original_url)
case class JsonFailedFetch(original_url: String, reason: String, message_type: String = "failed_fetch")
  extends BaseFetchJsonObject(message_type, original_url)