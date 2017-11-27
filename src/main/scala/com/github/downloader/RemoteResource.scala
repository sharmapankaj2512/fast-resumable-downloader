package com.github.downloader

import java.net.{HttpURLConnection, URL}

import akka.stream.scaladsl.Source
import akka.stream.scaladsl.StreamConverters._

import scala.util.Try

case class RemoteResource(Url: String, offset: Int = 0) {
  val connection: HttpURLConnection = httpRangeUrlConnection()

  def asStream(): Source[PartialResponse, Any] = {
    Try(connection.getInputStream)
      .map(stream => fromInputStream(() => stream)
        .map(PartialResponse(_, connection.getContentLength)))
      .getOrElse(Source.empty)
  }

  def httpRangeUrlConnection(): HttpURLConnection = {
    val connection = new URL(Url).openConnection().asInstanceOf[HttpURLConnection]
    connection.setRequestProperty("Range", s"bytes=$offset-")
    connection
  }
}
