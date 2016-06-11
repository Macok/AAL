package com.mac.aal.util

import scala.util.Random

/**
  * Created by mac on 05.06.16.
  */
object RandomUtils {

  def takeRandomN[A](n: Int, as: List[A]) =
    scala.util.Random.shuffle(as).take(n)

  def randomNumber(min: Int, max: Int): Int = {
    Random.nextInt(max - min + 1) + min
  }
}
