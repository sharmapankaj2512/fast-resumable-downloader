package com.github.downloader

import java.io.FileWriter

import akka.Done
import akka.actor.ActorSystem
import akka.stream.scaladsl.GraphDSL._
import akka.stream.scaladsl.RunnableGraph._
import akka.stream.scaladsl.{Broadcast, GraphDSL, Sink}
import akka.stream.{ActorMaterializer, ClosedShape}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class DownloadManager(subscriber: DownloadSubscriber) {
  implicit val system = ActorSystem("reactive-tweets")
  implicit val materializer = ActorMaterializer()

  def startDownload(url: String): Future[Done] = {
    val fileName = Math.abs(url.hashCode).toString
    val fileWriter = new FileWriter(fileName, true)
    val stream = RemoteResource(url).asStream()
    val progressBarSink: Sink[PartialResponse, Future[Done]] = Sink.foreach(pr => subscriber.notify(pr))
    val fileWriterSink: Sink[PartialResponse, Future[Done]] = Sink.foreach(pr => fileWriter.write(pr.body))

    val future = fromGraph(create(progressBarSink, fileWriterSink)((_, _)) { implicit builder =>
      (ps, fs) =>
        import GraphDSL.Implicits._

        val broadcast = builder.add(Broadcast[PartialResponse](2))

        stream ~> broadcast.in
        broadcast.out(0) ~> ps.in
        broadcast.out(1) ~> fs.in

        ClosedShape
    }).run()._2

    future.andThen { case _ => {
      system.terminate()
      fileWriter.close()
    }
    }
  }
}
