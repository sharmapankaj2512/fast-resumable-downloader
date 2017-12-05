package com.github.downloader.examples

import com.github.downloader.{CommandLineProgressBar, DownloadManager}

object DownloadVideoFile extends App {
  val url = "http://mirrors.standaloneinstaller.com/video-sample/jellyfish-25-mbps-hd-hevc.3gp"

  DownloadManager(CommandLineProgressBar()).startDownload(url)
}
