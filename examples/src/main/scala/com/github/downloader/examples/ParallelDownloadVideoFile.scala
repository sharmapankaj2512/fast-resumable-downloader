package com.github.downloader.examples

import com.github.downloader.{ParallelDownloader, RemoteResource}
import com.github.subscriber.CommandLineProgressBar

object ParallelDownloadVideoFile extends App {
  val url = "http://mirrors.standaloneinstaller.com/video-sample/jellyfish-25-mbps-hd-hevc.3gp"
  val progressBar = CommandLineProgressBar(RemoteResource(url).size())

  ParallelDownloader(progressBar).startDownload(url, 0)
}
