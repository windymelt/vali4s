package dev.capslock.vali4s.core

trait Schema[-A, +B] {
  def run(in: A): Either[SchemaFailed[A], B]
  // for pattern matching
  def unapply(in: A): Option[B] = run(in).toOption
}

extension [A, B](s: Schema[A, B]) {
  inline def |[C](ss: Schema[A, C]): Schema[A, B | C] = new Schema[A, B | C] {
    def run(in: A): Either[SchemaFailed[A], B | C] =
      s.run(in).orElse(ss.run(in))
  }
  inline def |>[C](ss: Schema[B, C]): Schema[A, C] = new Schema[A, C] {
    def run(in: A): Either[SchemaFailed[A], C] =
      s.run(in).flatMap(ss.run).left.map(_ => SchemaFailed(in))
  }
}
