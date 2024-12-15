package dev.capslock

import vali4s.core.{Schema, SchemaFailed}

package object vali4s {
  // Schema for any type
  private inline def isType[A]: Schema[Any, A] = new Schema[Any, A] {
    def run(in: Any): Either[SchemaFailed[Any], A] = in.isInstanceOf[A] match
      case true  => Right(in.asInstanceOf[A])
      case false => Left(new SchemaFailed(in))
  }
  val string = isType[String]
  val int    = isType[Int]
  val long   = isType[Long]
  val double = isType[Double]
  val float  = isType[Float]
  val bool   = isType[Boolean]

  // primitive combinators
  def pred[A](f: A => Boolean): Schema[A, A] = in =>
    f(in) match
      case true  => Right(in)
      case false => Left(new SchemaFailed(in))

  def seq[A, B](s: Schema[A, B]): Schema[Seq[A], Seq[B]] = in =>
    in.map(s.run)
      .foldLeft(Right(Seq.empty): Either[SchemaFailed[Seq[A]], Seq[B]]) {
        case (Right(acc), Right(b)) => Right(acc :+ b)
        case (Left(f), _)           => Left(new SchemaFailed(in))
        case (_, Left(f))           => Left(new SchemaFailed(in))
      }

  def transform[A, B](f: A => B): Schema[A, B] = in => Right(f(in))

  def as[A, B](s: B): Schema[A, B] = _ => Right(s)

  // concrete combinators

  // string
  def regex(r: scala.util.matching.Regex): Schema[String, String] =
    pred(r.matches)

  def startsWith(prefix: String): Schema[String, String] =
    pred(_.startsWith(prefix))

  def endsWith(suffix: String): Schema[String, String] =
    pred(_.endsWith(suffix))

  def exactly[A](a: A): Schema[A, A] =
    pred(_.equals(a))

  def inSize(range: Range): Schema[String, String] =
    pred(in => range.contains(in.length))

  def minLength(n: Int) = inSize(n to Int.MaxValue)
  def maxLength(n: Int) = inSize(0 until n)
}
