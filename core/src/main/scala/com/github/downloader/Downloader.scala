package com.github.downloader

import akka.Done
import akka.actor.ActorSystem
import akka.stream.scaladsl.GraphDSL._
import akka.stream.scaladsl.RunnableGraph._
import akka.stream.scaladsl.{Broadcast, GraphDSL, Sink}
import akka.stream.{ActorMaterializer, ClosedShape}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Downloader(progressBar: CommandLineProgressBar, file: File) {
  implicit val system = ActorSystem("downloader")
  implicit val materializer = ActorMaterializer()

  def startDownload(url: String, offset: Long): Future[Done] = {
    val stream = RemoteResource(url).asStream(offset)
    val pbSink: Sink[PartialResponse, Future[Done]] = Sink.foreach[PartialResponse](pr => progressBar.notify(pr))
    val fileSink: Sink[PartialResponse, Future[Done]] = Sink.foreach[PartialResponse](pr => file.notify(pr))

    cleanup(fromGraph(create(pbSink, fileSink)((_, _)) { implicit builder =>
      (ps, fs) =>
        import GraphDSL.Implicits._

        val broadcast = builder.add(Broadcast[PartialResponse](2))
        stream ~> broadcast.in
        broadcast ~> ps.in
        broadcast ~> fs.in
        ClosedShape
    }).run())
  }

  private def cleanup(tuple: (Future[Done], Future[Done])): Future[Done] = {
    tuple._2.andThen { case _ =>
      file.end()
      system.terminate()
    }
  }
}

object Downloader {
  def download(url: String) = {
    val file = File(url)
    val downloadedSize = file.downloadedSize()
    val progressBar = CommandLineProgressBar(RemoteResource(url).size())

    progressBar.tick(downloadedSize)
    Downloader(progressBar, file).startDownload(url, downloadedSize)
  }
}
