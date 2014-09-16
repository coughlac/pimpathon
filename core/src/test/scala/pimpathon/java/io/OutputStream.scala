package pimpathon.java.io

import java.io._
import org.junit.Test
import scala.util.{Failure, Success}

import org.junit.Assert._
import pimpathon.java.io.outputStream._
import pimpathon.util._
import pimpathon.any._


class OutputStreamTest {
  @Test def attemptClose: Unit = {
    assertEquals(Success(()), createOutputStream().attemptClose())
    assertEquals(Failure(boom), createOutputStream(onClose = () => throw boom).attemptClose())
  }

  @Test def closeAfter: Unit = {
    val os = createOutputStream()

    assertOutputStreamClosed(false, os.closed)
    assertEquals("result", os.closeAfter(_ => "result"))
    assertOutputStreamClosed(true, os.closed)
  }

  @Test def closeIf: Unit = {
    val os = createOutputStream()

    assertOutputStreamClosed(false, os.closed)
    assertOutputStreamClosed(false, os.closeIf(false).closed)
    assertOutputStreamClosed(true,  os.closeIf(true).closed)
  }

  @Test def closeUnless: Unit = {
    val os = createOutputStream()

    assertOutputStreamClosed(false, os.closed)
    assertOutputStreamClosed(false, os.closeUnless(true).closed)
    assertOutputStreamClosed(true,  os.closeUnless(false).closed)
  }

  @Test def drain: Unit = {
    for {
      expectedCloseIn  <- List(false, true)
      expectedCloseOut <- List(false, true)
      input            <- List("Input", "Repeat" * 100)
    } {
      val (is, os) = (createInputStream(input.getBytes), createOutputStream())

      os.drain(is, expectedCloseOut, expectedCloseIn)

      assertEquals(input, os.toString)
      assertOutputStreamClosed(expectedCloseOut, os.closed)
      assertInputStreamClosed(expectedCloseIn, is.closed)
    }

    ignoreExceptions {
      val (is, os) = (createInputStream(), createOutputStream())

      os.drain(is, closeOut = false)
      os.drain(is, closeIn = false)
      os.drain(is, closeOut = false, closeIn = false)
      os.drain(is, closeIn = false, closeOut = false)
    }
  }

  @Test def << : Unit = {
    val (is, os) = (createInputStream("content".getBytes), createOutputStream())

    os << is

    assertEquals("content", os.toString)
    assertOutputStreamClosed(false, os.closed)
    assertInputStreamClosed(false, is.closed)
  }

  @Test def buffered: Unit = {
    val (is, os) = (createInputStream("content".getBytes), createOutputStream())

    assertEquals("content", os.tap(o => (o.buffered: BufferedOutputStream).drain(is)).toString)
  }

  @Test def writeUpToN: Unit = {
    def write(text : String, n : Int) = {
      val (is, os) = (createInputStream(text.getBytes), createOutputStream())
      os.tap(_.closeAfter(_.writeUpToN(is, n))).toString
    }

    assertEquals("cont", write("contents", 4))
    assertEquals("contents", write("contents", 8))
    assertEquals("contents", write("contents", 9))
    assertEquals("", write("contents", 0))
    intercept[IllegalArgumentException](write("contents", -1))
  }

  @Test def writeN: Unit = {
    def write(text : String, n : Int) = {
      val (is, os) = (createInputStream(text.getBytes), createOutputStream())
      os.tap(_.closeAfter(_.writeN(is, n))).toString
    }

    assertEquals("cont", write("contents", 4))
    assertEquals("contents", write("contents", 8))
    assertEquals("", write("contents", 0))
    intercept[IllegalArgumentException](write("contents", -1))
    intercept[IOException](write("contents", 9))
  }
}
