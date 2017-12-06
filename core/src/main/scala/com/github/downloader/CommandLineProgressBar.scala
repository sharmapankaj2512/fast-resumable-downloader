package com.github.downloader

case class CommandLineProgressBar(totalSize: Float) {
  var progress:Float = 0

  def tick(chunkSize: Float): Unit = {
    progress += (chunkSize * 100.0f) / totalSize
    System.out.printf("\r\b Downloaded " + progress.asInstanceOf[Int] + "%%")
  }
}