package com.github.downloader

case class CommandLineProgressBar(totalSize: Float) extends DownloadSubscriber {
  var progress: Float = 0

  def tick(chunkSize: Float): Unit = {
    progress += (chunkSize * 100.0f) / totalSize
    System.out.printf("\r\b Downloaded " + progress.asInstanceOf[Int] + "%%")
  }

  override def notify(partialResponse: PartialResponse): Unit = {
    tick(partialResponse.size)
  }

  override def end(): Unit = {
    System.out.printf("\r\b Downloaded completed")
  }
}