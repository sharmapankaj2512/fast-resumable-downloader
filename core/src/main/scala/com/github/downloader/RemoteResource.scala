package com.github.downloader

import java.net.{HttpURLConnection, URL}

import akka.stream.scaladsl.Source
import akka.stream.scaladsl.StreamConverters._

import scala.util.Try

case class RemoteResource(url: String) {
  def size(): Int = {
    val connection = new URL(url).openConnection().asInstanceOf[HttpURLConnection]
    connection.setRequestMethod("HEAD")

    val size = Try(connection.getInputStream)
      .map(_ => connection.getContentLength)
      .getOrElse(0)

    connection.disconnect()
    size
  }

  def asStream(offset: Long = 0): Source[PartialResponse, Any] = {
    val connection = HttpRangeConnection(url, offset)

    Try(connection.getInputStream)
      .map(stream => fromInputStream(() => stream)
        .map(PartialResponse(_, connection.getContentLength)))
      .getOrElse(Source.empty)
  }
}
