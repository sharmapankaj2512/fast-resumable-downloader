package com.github.downloader

import akka.stream.scaladsl.Source
import akka.stream.scaladsl.StreamConverters._

import scala.util.Try

case class RemoteResource(url: String) {
  def asStream(offset: Int = 0): Source[PartialResponse, Any] = {
    val connection = HttpRangeConnection(url, offset)

    Try(connection.getInputStream)
      .map(stream => fromInputStream(() => stream)
        .map(PartialResponse(_, connection.getContentLength)))
      .getOrElse(Source.empty)
  }
}
