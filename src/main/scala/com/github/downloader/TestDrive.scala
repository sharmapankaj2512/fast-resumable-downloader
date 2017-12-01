package com.github.downloader

import java.io.FileWriter

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext.Implicits.global

object TestDrive extends App {
  implicit val system = ActorSystem("reactive-tweets")
  implicit val materializer = ActorMaterializer()

  val url = "http://mirrors.standaloneinstaller.com/video-sample/jellyfish-25-mbps-hd-hevc.3gp"
  val fileName = Math.abs(url.hashCode).toString
  val writer = new FileWriter(fileName, true)

  DownloadManager(CommandLineProgressBar())
    .startDownload(url, writer)
    .onComplete(_ => {
      writer.close()
      system.terminate()
    })
}
