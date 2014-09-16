package pimpathon

import _root_.java.io.{InputStream, OutputStream}
import _root_.java.math.BigInteger
import scala.math

import pimpathon.any._
import pimpathon.string._


object array {
  implicit class ByteArrayOps(val array: Array[Byte]) extends AnyVal {
    def toHex(length: Int): String = toHex.prefixPadTo(length, '0')
    def toHex: String = new BigInteger(1, array).toString(16)

    def copyUpToN(n: Long, is: InputStream, os: OutputStream): Int =
      readUpToN(n, is).tapUnless(_ == -1)(os.write(array, 0, _))

    def readUpToN(n: Long, is: InputStream): Int =
      if (n == 0) -1 else is.read(array, 0, math.min(n, array.length).toInt)
  }
}

