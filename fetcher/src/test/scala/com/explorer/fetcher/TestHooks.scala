package com.explorer.fetcher
import com.explorer.common.HttpHooks
import com.ning.http.client.Response
import com.ning.http.client.HttpResponseBodyPart

trait TestHooks {
  object TestHeaderHookFail extends HttpHooks {
    override def canContinueFromHeaders(response: Response, headers: Map[String, String]) = {
      false
    }
  }

  object TestHeaderHookPass extends HttpHooks {
    override def canContinueFromHeaders(response: Response, headers: Map[String, String]) = {
      true
    }
  }

  object TestStatusHookFail extends HttpHooks {
    override def canContinueFromStatusCode(response: Response, status: Integer) = {
      false
    }
  }

  object TestStatusHookPass extends HttpHooks {
    override def canContinueFromStatusCode(response: Response, status: Integer) = {
      true
    }
  }

  object TestBodyPartHookFail extends HttpHooks {
    override def canContinueFromBodyPartReceived(response: Response, part: HttpResponseBodyPart) = {
      response.getResponseBody().length < 1193
    }
  }

  object TestBodyPartHookPass extends HttpHooks {
    override def canContinueFromBodyPartReceived(response: Response, part: HttpResponseBodyPart) = {
      true
    }
  }
}