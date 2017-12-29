package com.github.downloader

import com.github.subscriber.{CommandLineProgressBar, File}

object CommandLineDownloader {
  def download(url: String): Unit = {
    val file = File(url)
    val downloadedSize = file.downloadedSize()
    val remoteResource = RemoteResource(url)
    val progressBar = CommandLineProgressBar(remoteResource.size())

    progressBar.tick(downloadedSize)
    SequentialDownloader(List(progressBar, file)).download(remoteResource, downloadedSize)
  }
}
