package pimpathon

import scala.language.implicitConversions

import scala.util.{Failure, Success, Try}

import pimpathon.function._


object either {
  implicit class EitherOps[L, R](val either: Either[L, R]) extends AnyVal {
    def leftMap[M](f: L ⇒ M): Either[M, R]  = bimap[M, R](f, identity[R])
    def rightMap[S](f: R ⇒ S): Either[L, S] = bimap[L, S](identity[L], f)

    def valueOr(lr: L ⇒ R): R = rightOr(lr)
    def valueOr(pf: PartialFunction[L, R]): Either[L, R] = rescue(pf)

    def rescue(lr: L ⇒ R): R = rightOr(lr)
    def rescue(pf: PartialFunction[L, R]): Either[L, R] = leftFlatMap(pf.either)

    def leftOr(rl: R ⇒ L): L  = either.fold(identity, rl)
    def rightOr(lr: L ⇒ R): R = either.fold(lr, identity)

    def bimap[M, S](lf: L ⇒ M, rf: R ⇒ S): Either[M, S] = either.fold(l ⇒ Left[M, S](lf(l)), r ⇒ Right[M, S](rf(r)))

    def leftFlatMap(f: L ⇒ Either[L, R]): Either[L, R]  = either.fold(f, Right(_))
    def rightFlatMap(f: R ⇒ Either[L, R]): Either[L, R] = either.fold(Left(_), f)

    def tap(l: L ⇒ Unit, r: R ⇒ Unit): Either[L, R] = { either.fold(l, r); either }

    def toTry(implicit ev: L <:< Throwable): Try[R] = either.fold(Failure(_), Success(_))
  }

  implicit def eitherToRightProjection[L, R](either: Either[L, R]): Either.RightProjection[L, R] = either.right
  implicit def rightProjectionToEither[L, R](rp: Either.RightProjection[L, R]): Either[L, R] = rp.e
}