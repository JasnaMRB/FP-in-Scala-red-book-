package ch4

import scala.{Either => _, Option => _, Some => _} // hide std library `Option`, `Some` and `Either`, since we are writing our own in this chapter

/**
  * @author jasnamrb
  */

// Ex. 4.1
sealed trait Option[+A] {
  def map[B](f: A => B): Option[B] = this match {
    case None => None
    case Some(a) => Some(f(a))
  }

  def getOrElse[B>:A](default: => B): B = this match {
    case None => default
    case Some(a) => a
  }

  def flatMap[B](f: A => Option[B]): Option[B] = this map(f) getOrElse None

  def orElse[B>:A](ob: => Option[B]): Option[B] = this map(Some(_)) getOrElse ob

  def filter(f: A => Boolean): Option[A] = this match {
    case Some(a) if f(a) => this
    case _ => None
  }
}
case class Some[+A](get: A) extends Option[A]
case object None extends Option[Nothing]

object Option {
  def failingFn(i: Int): Int = {
    val y: Int = throw new Exception("fail!") // `val y: Int = ...` declares `y` as having type `Int`, and sets it equal to the right hand side of the `=`.
    try {
      val x = 42 + 5
      x + y
    }
    catch { case e: Exception => 43 } // A `catch` block is just a pattern matching block like the ones we've seen. `case e: Exception` is a pattern that matches any `Exception`, and it binds this value to the identifier `e`. The match returns the value 43.
  }

  def failingFn2(i: Int): Int = {
    try {
      val x = 42 + 5
      x + ((throw new Exception("fail!")): Int) // A thrown Exception can be given any type; here we're annotating it with the type `Int`
    }
    catch { case e: Exception => 43 }
  }

  def mean(xs: Seq[Double]): Option[Double] =
    if (xs.isEmpty) None
    else Some(xs.sum / xs.length)

  // Ex. 4.2
  // the variance is the mean of math.pow(x - m, 2) for each element x in the sequence.
  // can also use map if you redefine
  def variance(xs: Seq[Double]): Option[Double] = mean(xs) flatMap (m => mean(xs.map(x => math.pow(x - m, 2))))

  /**
    *
    *   implicit class DoubleSeqOps(xs: Seq[Double]) {

    // Exercise 4.2
    lazy val toNel: Option[Seq[Double]] = xs match {
      case Nil => None
      case a => Some(a)
    }

    // Exercise 4.2
    lazy val mean: Option[Double] =
      xs.toNel.map(xs => xs.foldLeft(0.0)(_ + _) / xs.length)

    // Exercise 4.2
    lazy val variance: Option[Double] =
      mean map { m => xs.map(a => Math.pow(a - m, 2)).sum }

  }
    */


  
  // Ex 4.3
  def map2[A,B,C](a: Option[A], b: Option[B])(f: (A, B) => C): Option[C] = for {
    x <- a
    y <- b
  } yield f(x, y)

  // Ex. 4.4
  def sequence[A](a: List[Option[A]]): Option[List[A]] = a match {
    case Nil => Some(Nil)
    case x :: xs => x flatMap (xx => sequence(xs) map (xx :: _))
  }

  // Ex. 4.5
  def traverse[A, B](a: List[A])(f: A => Option[B]): Option[List[B]] = a match {
    case Nil => Some(Nil)
    case x :: xs => map2(f(x), traverse(xs)(f))(_ :: _)
  }
}
