package com.mac.aal.graph

import com.mac.aal.io.{RoadNetworkExporter, RoadNetworkImporter}
import com.mac.aal.json.CityJsonEntry
import com.mac.aal.util.{BinaryHeap, RandomUtils}
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.DefaultFormats

import scala.collection.mutable
import scala.io.Source
import scala.util.Random

/**
  * Created by mac on 05.06.16.
  */
case class RoadNetwork(cities: Map[String, City],
                       travelCostPerMinute: Double,
                       alliances: Set[Set[String]]) {

  def sameAlliance(city1: String, city2: String): Boolean = {
    alliances.exists(Set(city1, city2).subsetOf(_))
  }

  def cheapestPath(startId: String, endId: String): Option[List[String]] = {
    assert(cities.keySet.contains(startId) && cities.keySet.contains(endId))

    val predecessors = mutable.Map.empty[String, String]

    val prices = mutable.Map.empty[String, Double] +=
      (startId -> 0) ++=
      (cities.keySet - startId).map(s => s -> Double.MaxValue)

    val queue = new BinaryHeap[String]
    cities.keySet.foreach { c => queue +=(c, prices(c)) }

    while (queue.root._1 != endId) {
      val currentId = queue.pop._1
      val current = cities(currentId)

      current.adjacent.foreach {
        case (neighborId, roadParams) =>
          val newPrice = prices(currentId) + roadParams.price + roadParams.duration * travelCostPerMinute +
            (if (sameAlliance(currentId, neighborId))
              0
            else cities(neighborId).price)

          if (newPrice < prices(neighborId)) {
            prices += (neighborId -> newPrice)
            predecessors += (neighborId -> currentId)

            queue.update(neighborId, newPrice)
          }
      }

    }

    if (prices(endId) == Double.MaxValue) {
      return None
    }

    val result = mutable.MutableList.empty[String]
    result += endId
    var pre = predecessors.get(endId)
    while (pre.nonEmpty) {
      result += pre.get
      pre = predecessors.get(pre.get)
    }

    Some(result.reverse.toList)
  }

  def toFile(filename: String) = {
    RoadNetworkExporter(this, filename)
  }
}

object RoadNetwork {

  def fromFile(filename: String): RoadNetwork = {
    RoadNetworkImporter(filename)
  }

  def random(citiesCount: Int,
             roadsCount: Int,
             alliancesCount: Int,
             cityPriceMin: Int = 0,
             cityPriceMax: Int = 50,
             roadPriceMin: Int = 0,
             roadPriceMax: Int = 50,
             roadDurationMin: Int = 1,
             roadDurationMax: Int = 50,
             costPerMinute: Double = 0.5): RoadNetwork = {

    RandomGenerator(citiesCount,
      roadsCount,
      alliancesCount,
      cityPriceMin,
      cityPriceMax,
      roadPriceMin,
      roadPriceMax,
      roadDurationMin,
      roadDurationMax,
      costPerMinute)
  }
}
