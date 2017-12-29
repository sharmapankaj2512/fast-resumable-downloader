package com.github.downloader

trait Downloader {
  def download(remoteResource: RemoteResource, downloadedOffset: Long = 0): Unit
}
