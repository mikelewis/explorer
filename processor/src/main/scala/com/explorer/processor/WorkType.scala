package com.explorer.processor
import org.jsoup.nodes.Document

sealed trait WorkType

abstract class BaseProcessorWorkType(originalMsg: String) extends WorkType
case class ProcessCompletedFetchedUrl(originalMsg: String, originalUrl: String, finalUrl: String,
  status: Int, headers: Map[String, String], body: String)
  extends BaseProcessorWorkType(originalMsg)

case class ProcessFailedFetchedUrl(originalMsg: String, originalUrl: String, reason: String)
  extends BaseProcessorWorkType(originalMsg)

sealed trait HtmlProcessorWorkType extends WorkType

case class ParseHtml(url: String, html: String) extends HtmlProcessorWorkType
case class DoneParseHtml(document: Document, urls: List[String]) extends HtmlProcessorWorkType

sealed trait UrlProcessWorkType extends WorkType
case class ProcessUrl(url: String) extends UrlProcessWorkType
case class ProceedWithUrl(url: String, delay: Int) extends UrlProcessWorkType
case class DoNotProceedWithUrl(url: String) extends UrlProcessWorkType