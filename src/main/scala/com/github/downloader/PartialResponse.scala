package com.github.downloader

import akka.util.ByteString

case class PartialResponse(byteString: ByteString, actualSize: Int) {
  val body: String = byteString.utf8String
  val size: Int = byteString.size
}
