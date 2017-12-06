package com.github.downloader

import java.io.FileWriter

case class File(url: String) extends DownloadSubscriber {
  private val fileName = Math.abs(url.hashCode).toString
  private val fileWriter = new FileWriter(fileName, true)

  override def notify(partialResponse: PartialResponse): Unit = {
    fileWriter.write(partialResponse.body)
  }

  override def end(): Unit = {
    fileWriter.close()
  }
}
