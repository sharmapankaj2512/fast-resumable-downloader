package com.github.downloader

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorMaterializer, IOResult}
import akka.testkit.{ImplicitSender, TestKit}
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers, WordSpecLike}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class DownloaderSpec() extends TestKit(ActorSystem("DownloaderSpec")) with ImplicitSender with WordSpecLike
  with BeforeAndAfterEach with Matchers {

  val port = 8080
  val host = "localhost"
  val wireMockServer = new WireMockServer()
  val path = "/path/video.mp3"

  override def beforeEach {
    configureFor(host, port)
    wireMockServer.start()
  }

  override def afterEach() {
    wireMockServer.stop()
  }

  "Downloader with valid url" must {

    "return partial response" in {
      val url: String = s"http://$host:$port$path"
      implicit val materializer = ActorMaterializer()

      stubFor(get(urlEqualTo(path))
        .willReturn(aResponse()
          .withStatus(200)
          .withBody("hello")))

      val source: Source[PartialResponse, Future[IOResult]] = Downloader(url).download()
      val result = Await.result(source.runWith(Sink.seq), Duration(3, TimeUnit.SECONDS))

      assert(result.map(_.body).mkString == "hello")
      assert(result.map(_.size).sum == 5)
      assert(result.map(_.actualSize).sum == 5)
    }

  }
}