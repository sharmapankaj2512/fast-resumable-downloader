package com.github.downloader

import java.io.InputStream
import java.net.{HttpURLConnection, URL}

case class HttpRangeConnection(url: String, startOffset: Long = 0) {
  val connection: HttpURLConnection = new URL(url).openConnection().asInstanceOf[HttpURLConnection]
  connection.setRequestProperty("Range", s"bytes=$startOffset-")

  def getInputStream: InputStream = {
    connection.getInputStream
  }

  def getContentLength: Int = {
    connection.getContentLength
  }
}
