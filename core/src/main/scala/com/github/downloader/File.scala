package com.github.downloader

import java.io.{FileOutputStream, FileWriter}
import java.nio.file.{Files, Paths}

import scala.util.Try

case class File(url: String) extends DownloadSubscriber {
  private val fileName = Paths.get(url).getFileName.toString
  private val fileWriter = new FileOutputStream(fileName, true)

  def downloadedSize(): Long = {
    Try(Files.size(Paths.get(fileName))).getOrElse(0)
  }

  override def notify(partialResponse: PartialResponse): Unit = {
    fileWriter.write(partialResponse.byteString.toArray)
  }

  override def end(): Unit = {
    fileWriter.close()
  }
}
