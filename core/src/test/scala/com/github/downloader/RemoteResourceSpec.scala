package com.github.downloader

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import akka.testkit.{ImplicitSender, TestKit}
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpecLike}

import scala.concurrent.Await
import scala.concurrent.Await._
import scala.concurrent.duration._

class RemoteResourceSpec() extends TestKit(ActorSystem("DownloaderSpec")) with ImplicitSender with WordSpecLike
  with BeforeAndAfterEach with Matchers {

  "Downloader with valid url" must {
    "return stream of partial response" in {
      stubFor(get(urlEqualTo(Path))
        .willReturn(aResponse()
          .withStatus(200)
          .withBody("hello")))

      val sink = RemoteResource(Url).asStream().get.runWith(Sink.seq)
      val result = Await.result(sink, 3.seconds)

      result.map(_.bodyAsString) shouldBe Seq("hello")
      result.map(_.size).sum shouldBe 5
      result.map(_.actualSize).sum shouldBe 5
    }

    "return empty stream" in {
      stubFor(get(urlEqualTo(Path))
        .willReturn(aResponse()
          .withStatus(400)))

      RemoteResource(Url).asStream() shouldBe None
    }

    "return size of remote resource" in {
      stubFor(head(urlEqualTo(Path))
        .willReturn(aResponse()
          .withStatus(200)
          .withBody("hello")))


      RemoteResource(Url).size() shouldBe 5
    }
  }

  "Downloader with non-existent url" must {
    "return empty stream" in {
      stubFor(get(urlEqualTo("/not/found/path"))
        .willReturn(aResponse()
          .withStatus(404)))

      RemoteResource(Url).asStream() shouldBe None
    }
  }

  override def beforeEach {
    configureFor(Host, Port)
    wireMockServer.start()
  }

  override def afterEach() {
    wireMockServer.stop()
  }

  val Port = 8080
  val Host = "localhost"
  val Path = "/path/video.mp3"
  val Url: String = s"http://$Host:$Port$Path"

  val wireMockServer = new WireMockServer()
  implicit val materialize: ActorMaterializer = ActorMaterializer()
}