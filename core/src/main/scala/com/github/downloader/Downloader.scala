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

  def startDownload(url: String): Future[Done] = {
    val stream = RemoteResource(url).asStream()
    val pbSink: Sink[PartialResponse, Future[Done]] = Sink.foreach[PartialResponse](pr => progressBar.notify(pr))
    val fileSink: Sink[PartialResponse, Future[Done]] = Sink.foreach[PartialResponse](pr => file.notify(pr))

    fromGraph(create(pbSink, fileSink)((_, _)) { implicit builder =>
      (ps, fs) =>
        import GraphDSL.Implicits._

        val broadcast = builder.add(Broadcast[PartialResponse](2))
        stream ~> broadcast.in
        broadcast.out(0) ~> ps.in
        broadcast.out(1) ~> fs.in
        ClosedShape
    }).run()._2.andThen { case _ =>
      file.end()
      system.terminate()
    }
  }
}
