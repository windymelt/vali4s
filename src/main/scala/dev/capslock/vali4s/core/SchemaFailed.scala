package dev.capslock.vali4s.core

class SchemaFailed[-A](attempted: A) {
  override def toString = s"Failed to parse: $attempted"
}
