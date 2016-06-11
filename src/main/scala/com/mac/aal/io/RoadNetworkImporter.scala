package com.mac.aal.io

import com.mac.aal.graph.{City, RoadNetwork, Road}
import com.mac.aal.json.CityJsonEntry
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.collection.mutable
import scala.io.Source

/**
  * Created by mac on 08.06.16.
  */
object RoadNetworkImporter {

  def apply(fileName: String): RoadNetwork = {
    val nodes = mutable.Map.empty[String, City]

    implicit val formats = DefaultFormats

    val root: JValue = parse(Source.fromFile(fileName).getLines.mkString)
    val cities = (root \ "cities").extract[List[CityJsonEntry]]
    val roads = (root \ "roads").extract[List[Road]]
    val alliances = (root \ "alliances").extract[Set[Set[String]]]

    for (city <- cities) {
      nodes += (city.id -> City(Map(), city.id, city.price))
    }

    for (road <- roads) {
      val orig = nodes(road.orig)
      val dest = nodes(road.dest)

      nodes(road.orig) = orig.copy(adjacent = orig.adjacent +
        (road.dest -> Road(road.orig, road.dest, road.price, road.duration)))
      nodes(road.dest) = dest.copy(adjacent = dest.adjacent +
        (road.orig -> Road(road.dest, road.orig, road.price, road.duration)))
    }

    RoadNetwork(nodes.toMap, (root \ "costPerMinute").extract[Double], alliances)
  }
}
