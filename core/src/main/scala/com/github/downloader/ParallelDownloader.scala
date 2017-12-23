package com.github.downloader

import java.io._
import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.FileIO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ParallelDownloader() {
  implicit val system: ActorSystem = ActorSystem("downloader")
  implicit val materialize: ActorMaterializer = ActorMaterializer()

  def startDownload(url: String, offset: Long): Unit = {
    val fileName = Paths.get(url).getFileName.toString
    val partFileNamePrefix = s"part-$fileName"

    Future.sequence(RemoteResource(url).asParallelStream()
      .map(s => s.map(pr => pr.byteString))
      .zipWithIndex.map({ case (stream, index) => FileIO.toPath(new File(s"$partFileNamePrefix-$index").toPath)
      .runWith(stream)
    })).onComplete(_ => {
      FileMerger(partFileNamePrefix, fileName).merge()
      system.terminate()
    })
  }
}
