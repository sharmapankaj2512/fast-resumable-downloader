package com.github.downloader

import akka.Done
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.stream.scaladsl.{Broadcast, GraphDSL, RunnableGraph, Sink}

import scala.concurrent.Future

case class DownloadManager(progressBar: CommandLineProgressBar) {
  def startDownload(url: String)(implicit system: ActorSystem, materializer: ActorMaterializer) = {
    val stream = RemoteResource(url).asStream()
    val progressBarSink: Sink[PartialResponse, Future[Done]] = Sink.foreach(pr => progressBar.tick(pr))

    RunnableGraph.fromGraph(GraphDSL.create(progressBarSink) { implicit builder =>
      ps =>
        import GraphDSL.Implicits._

        val broadcast = builder.add(Broadcast[PartialResponse](1))

        stream ~> broadcast.in
        broadcast.out(0) ~> ps.in

        ClosedShape
    }).run()
  }
}
