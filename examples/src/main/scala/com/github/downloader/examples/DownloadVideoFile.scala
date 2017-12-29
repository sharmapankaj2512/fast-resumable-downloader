package com.github.downloader.examples

import com.github.downloader.{RemoteResource, SequentialDownloader}
import com.github.subscriber.{CommandLineProgressBar, File}

object DownloadVideoFile extends App {
  val url = "http://mirrors.standaloneinstaller.com/video-sample/jellyfish-25-mbps-hd-hevc.3gp"

  val file = File(url)
  val downloadedSize = file.downloadedSize()
  val remoteResource = RemoteResource(url, downloadedSize)
  val progressBar = CommandLineProgressBar(remoteResource.size())
  progressBar.tick(downloadedSize)

  SequentialDownloader(List(progressBar, file))
    .download(remoteResource)
}
