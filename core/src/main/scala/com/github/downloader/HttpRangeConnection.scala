package com.github.downloader

import java.io.InputStream
import java.net.{HttpURLConnection, URL}

case class HttpRangeConnection(url: String, startOffset: Int = 0, endOffset: Int = -1) {
  val connection: HttpURLConnection = new URL(url).openConnection().asInstanceOf[HttpURLConnection]
  if (endOffset == -1)
    connection.setRequestProperty("Range", s"bytes=$startOffset-")
  else
    connection.setRequestProperty("Range", s"bytes=$startOffset-$endOffset")

  def getInputStream: InputStream = {
    connection.getInputStream
  }

  def getContentLength: Int = {
    connection.getContentLength
  }
}
