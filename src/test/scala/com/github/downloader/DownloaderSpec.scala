package com.github.downloader

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import akka.testkit.{ImplicitSender, TestKit}
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpecLike}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class DownloaderSpec() extends TestKit(ActorSystem("DownloaderSpec")) with ImplicitSender with WordSpecLike
  with BeforeAndAfterEach with Matchers {
  val port = 8080
  val host = "localhost"
  val path = "/path/video.mp3"
  val url: String = s"http://$host:$port$path"
  val wireMockServer = new WireMockServer()

  implicit val materializer = ActorMaterializer()

  "Downloader with valid url" must {
    "return stream of partial response" in {
      stubFor(get(urlEqualTo(path))
        .willReturn(aResponse()
          .withStatus(200)
          .withBody("hello")))

      val source = Downloader(url).download().get
      val result = Await.result(source.runWith(Sink.seq), Duration(3, TimeUnit.SECONDS))

      assert(result.map(_.body).mkString == "hello")
      assert(result.map(_.size).sum == 5)
      assert(result.map(_.actualSize).sum == 5)
    }

    "return empty stream" in {
      stubFor(get(urlEqualTo(path))
        .willReturn(aResponse()
          .withStatus(400)))

      assert(Downloader(url).download().isFailure)
    }
  }

  "Downloader with non-existent url" must {
    "return empty stream" in {
      stubFor(get(urlEqualTo("/not/found/path"))
        .willReturn(aResponse()
          .withStatus(404)))

      assert(Downloader(url).download().isFailure)
    }
  }

  override def beforeEach {
    configureFor(host, port)
    wireMockServer.start()
  }

  override def afterEach() {
    wireMockServer.stop()
  }
}