package com.github.downloader.examples

import com.github.downloader.ParallelDownloader

object ParallelDownloadVideoFile extends App {
  val url = "http://mirrors.standaloneinstaller.com/video-sample/jellyfish-25-mbps-hd-hevc.3gp"

  ParallelDownloader().startDownload(url, 0)
}
