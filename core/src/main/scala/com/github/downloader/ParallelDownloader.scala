package com.github.downloader

import java.io._
import java.nio.file.Paths

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Broadcast, FileIO, Sink}
import akka.util.ByteString
import com.github.subscriber.DownloadSubscriber

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ParallelDownloader(subscriber: DownloadSubscriber) {
  implicit val system: ActorSystem = ActorSystem("downloader")
  implicit val materialize: ActorMaterializer = ActorMaterializer()

  def startDownload(remoteResource: RemoteResource, offset: Long = 0): Unit = {
    val fileName = Paths.get(remoteResource.url).getFileName.toString
    val partFileNamePrefix = s"part-$fileName"
    val actualSize = remoteResource.size()

    Future.sequence(remoteResource.asParallelStream()
      .map(s => s.map(pr => pr.byteString))
      .zipWithIndex.map({ case (stream, index) => sink(partFileNamePrefix, index, actualSize).runWith(stream) }))
      .onComplete(_ => {
        FileMerger(partFileNamePrefix, fileName).merge()
        subscriber.completed()
        system.terminate()
      })
  }

  private def sink(partFileNamePrefix: String, index: Int, actualSize: Int): Sink[ByteString, NotUsed] = {
    val fileSink = FileIO.toPath(new File(s"$partFileNamePrefix-$index").toPath)
    val subscriberSink = Sink.foreach[ByteString](bs => subscriber.notify(PartialResponse(bs, actualSize)))
    Sink.combine(subscriberSink, fileSink)(Broadcast(_))
  }
}
