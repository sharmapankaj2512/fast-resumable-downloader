package com.github.downloader

trait Downloader {
  def download(remoteResource: RemoteResource): Unit
}
