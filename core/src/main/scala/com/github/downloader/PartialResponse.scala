package com.github.downloader

import akka.util.ByteString

case class PartialResponse(private val byteString: ByteString, actualSize: Int) {
  val bodyAsString: String = byteString.utf8String
  val body: Array[Byte] = byteString.toArray
  val size: Int = byteString.size
}
