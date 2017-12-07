package com.github.subscriber

import com.github.downloader.PartialResponse

trait DownloadSubscriber {
  def notify(partialResponse: PartialResponse)
  def completed(): Unit
}
