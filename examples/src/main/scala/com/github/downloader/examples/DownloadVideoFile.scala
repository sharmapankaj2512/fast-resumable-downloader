package com.github.downloader.examples

import com.github.downloader.{CommandLineProgressBar, DownloadManager, RemoteResource}

object DownloadVideoFile extends App {
  val url = "http://mirrors.standaloneinstaller.com/video-sample/jellyfish-25-mbps-hd-hevc.3gp"

  val totalSize = RemoteResource(url).size()
  DownloadManager(CommandLineProgressBar(totalSize)).startDownload(url)
}
