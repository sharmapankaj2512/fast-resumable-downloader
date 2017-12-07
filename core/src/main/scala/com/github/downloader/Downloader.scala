package com.github.downloader

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Broadcast, Sink}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Downloader(subscribers: List[DownloadSubscriber]) {
  implicit val system: ActorSystem = ActorSystem("downloader")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  def startDownload(url: String, offset: Long) = {
    val sinks = subscribers.map(subscriber => Sink.foreach[PartialResponse](pr => subscriber.notify(pr)))
    val combined: Sink[PartialResponse, NotUsed] = Sink.combine(sinks.head, sinks.tail.head)(Broadcast(_))

    RemoteResource(url)
      .asStream(offset)
      .map(stream => combined.runWith(stream))
      .getOrElse(Future.unit)
      .onComplete(_ => {
        subscribers.foreach(subscriber => subscriber.completed())
        system.terminate()
      })
  }
}

object Downloader {
  def download(url: String) = {
    val file = File(url)
    val downloadedSize = file.downloadedSize()
    val progressBar = CommandLineProgressBar(RemoteResource(url).size())

    progressBar.tick(downloadedSize)
    Downloader(List(progressBar, file)).startDownload(url, downloadedSize)
  }
}
