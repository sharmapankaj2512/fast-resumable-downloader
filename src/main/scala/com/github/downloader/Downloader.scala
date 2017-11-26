package com.github.downloader

import java.net.{URL, URLConnection}

import akka.stream.IOResult
import akka.stream.scaladsl.Source
import akka.stream.scaladsl.StreamConverters._

import scala.concurrent.Future

case class Downloader(Url: String, offset:Int = 0) {
  val connection: URLConnection = new URL(Url).openConnection()

  def download(): Source[PartialResponse, Future[IOResult]] = {
    connection.setRequestProperty("Range", s"bytes=$offset-")
    fromInputStream(connection.getInputStream)
      .map(PartialResponse(_, connection.getContentLength))
  }
}
