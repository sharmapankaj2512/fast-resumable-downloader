package com.github.downloader

object Chunkifier {
  def chunkify(totalSize: Int, chunkSize: Int): List[(Int, Int)] = {
    if (totalSize > chunkSize) {
      val sizes = Range.inclusive(0, totalSize, chunkSize).toList
      sizes.zip(sizes.tail)
    } else {
      List((0, totalSize))
    }
  }
}
