package com.explorer.common
import java.util.zip.CRC32

object HashUtil {
  def crc32(byteArray: Array[Byte]): Long = {
    val c = new CRC32
    c.update(byteArray)
    c.getValue
  }

  def crc32(str: String): Long = {
    crc32(str.getBytes)
  }
}