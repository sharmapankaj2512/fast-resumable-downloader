package com.github.downloader.examples

import com.github.downloader.CommandLineDownloader

object DownloadVideoFile extends App {
  val url = "http://mirrors.standaloneinstaller.com/video-sample/jellyfish-25-mbps-hd-hevc.3gp"

  CommandLineDownloader.download(url)
}
