package pimpathon

import org.junit.Test

import org.junit.Assert._
import pimpathon.boolean._
import pimpathon.util._


class BooleanTest {
  @Test def asInt(): Unit = assertEquals(List(0, 1), List(false, true).map(_.asInt))

  @Test def either_or(): Unit = assertEquals(
    List(Right(123), Left("456")),
    List(true, false).map(_.either(123).or("456"))
  )
}