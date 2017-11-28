package com.github.downloader

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink

import scala.concurrent.Await
import scala.concurrent.duration._

object TestDrive extends App {
  implicit val system = ActorSystem("reactive-tweets")
  implicit val materializer = ActorMaterializer()

  val result = Await.result(RemoteResource("http://mirrors.standaloneinstaller.com/video-sample/jellyfish-25-mbps-hd-hevc.3gp")
    .asStream()
    .map(_.body)
    .runWith(Sink.seq), 20.seconds)

  println(result)

  system.terminate()
}
