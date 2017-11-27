package com.github.downloader

import java.net.{HttpURLConnection, URL}

import akka.stream.scaladsl.Source
import akka.stream.scaladsl.StreamConverters._

import scala.util.Try

case class RemoteResource(Url: String) {
  def asStream(offset: Int = 0): Source[PartialResponse, Any] = {
    val connection = httpRangeUrlConnection(offset)

    Try(connection.getInputStream)
      .map(stream => fromInputStream(() => stream)
        .map(PartialResponse(_, connection.getContentLength)))
      .getOrElse(Source.empty)
  }

  private def httpRangeUrlConnection(offset: Int): HttpURLConnection = {
    val connection = new URL(Url).openConnection().asInstanceOf[HttpURLConnection]
    connection.setRequestProperty("Range", s"bytes=$offset-")
    connection
  }
}
