package com.github.downloader

import com.github.subscriber.{CommandLineProgressBar, File}

object CommandLineDownloader {
  def download(url: String): Unit = {
    val file = File(url)
    val downloadedSize = file.downloadedSize()
    val progressBar = CommandLineProgressBar(RemoteResource(url).size())

    progressBar.tick(downloadedSize)
    Downloader(List(progressBar, file)).startDownload(url, downloadedSize)
  }
}
