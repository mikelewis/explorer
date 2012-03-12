package com.explorer.processor
import org.jsoup.nodes.Document

sealed trait WorkType
//abstract class BaseProcessorWorkType(originalMsg: String) extends WorkType
case class ProcessCompletedFetchedUrl(originalMsg: String, originalUrl: String, finalUrl: String,
  status: Int, headers: Map[String, String], body: String) extends WorkType
case class ProcessFailedFetchedUrl(originalMsg: String, originalUrl: String, reason: String) extends WorkType

sealed trait HtmlProcessorWorkType extends WorkType

case class ParseHtml(url: String, html: String) extends HtmlProcessorWorkType
case class DoneParseHtml(document: Document, urls: List[String]) extends HtmlProcessorWorkType

sealed trait UrlProcessWorkType extends WorkType
case class ProcessUrls(urls: List[String]) extends UrlProcessWorkType
case class DoneProcessUrls(urlsWithDelay: List[(String, Int)]) extends UrlProcessWorkType