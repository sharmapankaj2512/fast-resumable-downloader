package com.github.downloader

import java.io._
import java.nio.file.{Files, Paths}
import java.util.Collections

import akka.actor.ActorSystem
import akka.stream.scaladsl.FileIO
import akka.stream.{ActorMaterializer, IOResult}

import scala.collection.JavaConverters._
import scala.collection.immutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ParallelDownloader() {
  implicit val system: ActorSystem = ActorSystem("downloader")
  implicit val materialize: ActorMaterializer = ActorMaterializer()

  def startDownload(url: String, offset: Long): Unit = {
    val fileName = Paths.get(url).getFileName.toString

    val result: immutable.Seq[Future[IOResult]] = RemoteResource(url).asParallelStream()
      .map(s => s.map(pr => pr.byteString))
      .zipWithIndex.map({ case (stream, index) => FileIO.toPath(new File(s"part-$fileName-$index").toPath)
      .runWith(stream)
    })

    Future.sequence(result)
      .onComplete(_ => {
        val files = new File(".").listFiles()
          .toList
          .filter(f => f.getName.startsWith("part-"))
          .sorted

        val streams = files.map(f => new FileInputStream(f)).asJavaCollection
        val in = new SequenceInputStream(Collections.enumeration[FileInputStream](streams))

        Files.copy(in, Paths.get(fileName))

        streams.forEach(f => f.close())
        files.foreach(f => f.delete())
        in.close()

        system.terminate()
      })
  }
}
