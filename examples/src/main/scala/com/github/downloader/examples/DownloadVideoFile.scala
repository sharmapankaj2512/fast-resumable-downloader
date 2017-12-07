package com.github.downloader.examples

import com.github.downloader.Downloader

object DownloadVideoFile extends App {
  Downloader.download("http://mirrors.standaloneinstaller.com/video-sample/jellyfish-25-mbps-hd-hevc.3gp")
}
