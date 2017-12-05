package com.github.downloader

case class CommandLineProgressBar(var progress: Float = 0f) {

  def tick(partialResponse: PartialResponse): Unit = {
    progress += (partialResponse.size * 100.0f) / partialResponse.actualSize
    System.out.printf("\r\b Download progress ... " + progress.asInstanceOf[Int] + "%%")
  }
}