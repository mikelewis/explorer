package com.explorer.common
import java.util.zip.CRC32
import scala.collection.TraversableLike

trait Crc32 {
  type GetBytesType = { def getBytes(): Array[Byte] }

  def crc32(byteArray: Array[Byte]): Long = {
    val c = new CRC32
    c.update(byteArray)
    c.getValue
  }

  def crc32(obj: GetBytesType): Long = crc32(obj.getBytes())

  def crc32(xs: Traversable[Any { def toByte: Byte }]): Long = crc32(xs.map(_.toByte).toArray)
}