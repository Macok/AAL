package com.mac.aal

import java.io.File

import com.mac.aal.Mode._
import com.mac.aal.graph.RoadNetwork
import com.mac.aal.io.Visualizer
import com.mac.aal.util.RandomUtils

import scala.collection.mutable

/**
  * Created by mac on 05.06.16.
  */
object Main {

  def main(args: Array[String]) {

    val argsParser = new scopt.OptionParser[Config]("AAL") {
      head("AAL path finder", "1.0")

      opt[File]("input") action { (i, c) => c.copy(input = i) }
      opt[String]("cityStart") action { (s, c) => c.copy(cityStart = s) }
      opt[String]("cityEnd") action { (s, c) => c.copy(cityEnd = s) }

      cmd("generate") action { (_, c) =>
        c.copy(mode = Generate)
      } children {
        opt[Int]("citiesCount") action { (i, c) => c.copy(citiesCount = i) }
        opt[Int]("roadsCount") action { (i, c) => c.copy(roadsCount = i) }
        opt[Int]("alliancesCount") action { (i, c) => c.copy(alliancesCount = i) }
        opt[Int]("cityPriceMin") action { (i, c) => c.copy(cityPriceMin = i) }
        opt[Int]("cityPriceMax") action { (i, c) => c.copy(cityPriceMax = i) }
        opt[Int]("roadPriceMin") action { (i, c) => c.copy(roadPriceMin = i) }
        opt[Int]("roadPriceMax") action { (i, c) => c.copy(roadPriceMax = i) }
        opt[Int]("roadDurationMin") action { (i, c) => c.copy(roadDurationMin = i) }
        opt[Int]("roadDurationMax") action { (i, c) => c.copy(roadDurationMax = i) }
        opt[Int]("costPerMinute") action { (i, c) => c.copy(costPerMinute = i) }
        opt[File]("output") action { (o, c) => c.copy(output = o) }
      }

      cmd("visualize") action { (_, c) =>
        c.copy(mode = Visualize)
      }

      cmd("performance-test") action { (_, c) =>
        c.copy(mode = PerformanceTest)
      } children {
        opt[Double]("density") action {
          (i, c) => c.copy(density = i)
        }
        opt[Int]("dimensionMin") action { (i, c) => c.copy(testDimensionMin = i) }
        opt[Int]("dimensionMax") action { (i, c) => c.copy(testDimensionMax = i) }
        opt[Int]("dimensionStep") action { (i, c) => c.copy(testDimensionStep = i) }
      }

      checkConfig {
        c => c.mode match {
          case Generate =>
            if (c.citiesCount == 0)
              failure("citiesCount not specified")
            if (c.roadsCount == 0)
              failure("roadsCount not specified")
            if (c.alliancesCount == 0)
              failure("alliancesCount not specified")
            else
              success

          case Visualize | Solve =>
            if (c.input.getName.isEmpty)
              failure("input file not specified")
            else success

          case _ =>
            success
        }
      }
    }

    argsParser.parse(args, Config(mode = Solve)) match {
      case Some(config) =>
        config.mode match {
          case Solve =>
            val network = RoadNetwork.fromFile(config.input.getAbsolutePath)
            val cheapest = network.cheapestPath(config.cityStart, config.cityEnd)

            cheapest match {
              case Some(road) => println(road)
              case None => println("no path")
            }

          case Generate =>
            val network = RoadNetwork.random(
              config.citiesCount,
              config.roadsCount,
              config.alliancesCount,
              config.cityPriceMax,
              config.cityPriceMax,
              config.roadDurationMin,
              config.roadPriceMax,
              config.roadDurationMin,
              config.roadDurationMax,
              config.costPerMinute)

            network.toFile(config.output.getAbsolutePath)

          case Visualize =>
            val network = RoadNetwork.fromFile(config.input.getAbsolutePath)

            Visualizer(network).visualize()

          case PerformanceTest =>
            val density = config.density
            println(s"Running performance test for graph density $density")
            val problemDimensions: Range =
              config.testDimensionMin to config.testDimensionMax by config.testDimensionStep
            val dimensionToSolvingTime = mutable.Map.empty[(Int, Int, Int), Double]
            for (i <- problemDimensions) {

              val V: Int = i
              val E: Int = ((math.pow(V, 2) / 2) * density).toInt
              val G: Int = math.sqrt(V).toInt

              println(s"Generating graph with $V vertices, $E edges and $G alliances...")

              val network = RoadNetwork.random(V, E, G)

              println("Solving cheapest path problem for 10 random pairs of cities...")
              val citiesIds = network.cities.keySet

              val allCitiesPairs = citiesIds.toList.combinations(2).toList

              val numSamples: Int = math.min(allCitiesPairs.size, 10)

              val randomCitiesPairs: List[List[String]] =
                RandomUtils.takeRandomN(numSamples, allCitiesPairs)

              val timeStart = System.currentTimeMillis()

              randomCitiesPairs.foreach {
                cities =>
                  val p = network.cheapestPath(cities.head, cities.tail.head)
              }

              val timeDiff = System.currentTimeMillis() - timeStart

              val avgTime = timeDiff.toDouble / numSamples

              dimensionToSolvingTime.put((V, E, G), avgTime)
            }

            println()
            println("Performance test results:")
            println("n (V,E,G)/ t(n) / q(n)")
            val lnOf2 = scala.math.log(2)
            val timesSortedByDim: List[((Int, Int, Int), Double)] = dimensionToSolvingTime.toList.sortBy(_._1._1).drop(10)
            val median: ((Int, Int, Int), Double) = timesSortedByDim(timesSortedByDim.size / 2)
            val tMedianActual = median._2
            val medianDim: (Int, Int, Int) = median._1
            val tMedianTheoretical = (math.log(medianDim._1) / lnOf2) * medianDim._2 * medianDim._3
            timesSortedByDim foreach {
              case (dim, time) =>
                val V = dim._1
                val E = dim._2
                val G = dim._3
                val timeTheoretical = (math.log(V) / lnOf2) * E * G
                val q = (time * tMedianTheoretical) / (timeTheoretical * tMedianActual)
                println(s"($V/$E/$G) / $time / $q")
            }
        }
      case None =>
    }
  }

}
