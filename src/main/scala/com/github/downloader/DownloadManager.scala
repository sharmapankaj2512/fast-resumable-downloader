package com.github.downloader

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink

case class DownloadManager(progressBar: CommandLineProgressBar) {
  def startDownload(url: String)(implicit system: ActorSystem, materializer: ActorMaterializer) = {
    val stream = RemoteResource(url).asStream()
    stream.runWith(Sink.foreach(pr => progressBar.tick(pr)))
  }
}
