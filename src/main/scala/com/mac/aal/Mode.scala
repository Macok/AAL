package com.mac.aal

/**
  * Created by mac on 08.06.16.
  */

object Mode {

  trait Mode

  case object Solve extends Mode

  case object Visualize extends Mode

  case object Generate extends Mode

  case object PerformanceTest extends Mode

}