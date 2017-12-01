package com.github.downloader

import java.io.FileWriter

import akka.Done
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Broadcast, GraphDSL, RunnableGraph, Sink}
import akka.stream.{ActorMaterializer, ClosedShape}

import scala.concurrent.Future

case class DownloadManager(progressBar: CommandLineProgressBar) {
  def startDownload(url: String, fileWriter: FileWriter)(implicit system: ActorSystem, materializer: ActorMaterializer):
  Future[Done] = {
    val stream = RemoteResource(url).asStream()
    val progressBarSink: Sink[PartialResponse, Future[Done]] = Sink.foreach(pr => progressBar.tick(pr))
    val fileWriterSink: Sink[PartialResponse, Future[Done]] = Sink.foreach(pr => fileWriter.write(pr.body))

    RunnableGraph.fromGraph(GraphDSL.create(progressBarSink, fileWriterSink)((_, _)) { implicit builder =>
      (ps, fs) =>
        import GraphDSL.Implicits._

        val broadcast = builder.add(Broadcast[PartialResponse](2))

        stream ~> broadcast.in
        broadcast.out(0) ~> ps.in
        broadcast.out(1) ~> fs.in

        ClosedShape
    }).run()._2
  }
}
