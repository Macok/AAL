package com.mac.aal.graph

import com.mac.aal.util.RandomUtils

import scala.collection.mutable

/**
  * Created by mac on 08.06.16.
  */
object RandomGenerator {
  def apply(citiesCount: Int,
            roadsCount: Int,
            alliancesCount: Int,
            cityPriceMin: Int,
            cityPriceMax: Int,
            roadPriceMin: Int,
            roadPriceMax: Int,
            roadDurationMin: Int,
            roadDurationMax: Int,
            costPerMinute: Double) = {
    assert(citiesCount > 2)
    assert(roadsCount > citiesCount)
    assert(roadsCount < citiesCount * (citiesCount - 1) / 2)

    val cities = mutable.Map[String, City]()

    cities ++= 1 to citiesCount map {
      i =>
        val cityId = "city" + i
        val cityPrice = RandomUtils.randomNumber(cityPriceMin, cityPriceMax)
        cityId -> City(Map(), cityId, cityPrice)
    }

    val allPossibleRoads = cities.keys.toList.combinations(2).toList map { l => (l.head, l.tail.head) }

    RandomUtils.takeRandomN(roadsCount, allPossibleRoads) foreach {
      case (city1, city2) =>
        assert(city1 != city2)

        val orig = cities(city1)
        val dest = cities(city2)
        val roadDuration = RandomUtils.randomNumber(roadDurationMin, roadDurationMax)
        val roadPrice = RandomUtils.randomNumber(roadPriceMin, roadPriceMax)
        cities(city1) = orig.copy(adjacent = orig.adjacent +
          (city2 -> Road(city2, city1, roadPrice, roadDuration)))
        cities(city2) = dest.copy(adjacent = dest.adjacent +
          (city1 -> Road(city1, city2, roadPrice, roadDuration)))
    }

    val alliances = 1 to alliancesCount map { _ =>
      val allianceMaxSize: Int = math.max(2, math.pow(citiesCount, -3)).toInt
      val allianceSize = RandomUtils.randomNumber(2, allianceMaxSize)
      RandomUtils.takeRandomN(allianceSize, cities.keys.toList).toSet
    } toSet

    RoadNetwork(cities.toMap, costPerMinute, alliances)
  }
}
