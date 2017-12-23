package com.github.downloader.examples

import com.github.downloader.{ParallelDownloader, RemoteResource}
import com.github.subscriber.CommandLineProgressBar

object ParallelDownloadVideoFile extends App {
  val url = "http://mirrors.standaloneinstaller.com/video-sample/jellyfish-25-mbps-hd-hevc.3gp"
  val resource = RemoteResource(url)
  val progressBar = CommandLineProgressBar(resource.size())

  ParallelDownloader(progressBar).startDownload(resource)
}
