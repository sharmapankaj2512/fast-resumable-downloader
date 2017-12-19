package com.github.downloader

import org.scalatest.{FlatSpec, Matchers}

class ChunkifierSpec  extends FlatSpec with Matchers {
  "Chunkifier" should "return valid chunk size pairs" in {
    Chunkifier.chunkify(10000002, 5000000) shouldBe List((0, 5000000), (5000001, 10000000), (10000001, 10000002))
    Chunkifier.chunkify(100, 5000000) shouldBe List((0, 100))
  }
}