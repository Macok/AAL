package com.mac.aal.io

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}

import com.mac.aal.graph.{City, RoadNetwork}
import com.mac.aal.json.CityJsonEntry
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.Extraction
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization


/**
  * Created by mac on 08.06.16.
  */
object RoadNetworkExporter {

  implicit val formats = org.json4s.DefaultFormats

  def apply(network: RoadNetwork, fileName: String) = {

    val jsonStr: String = roadNetworkToJsonString(network)

    Files.write(Paths.get(fileName), jsonStr.getBytes(StandardCharsets.UTF_8))
  }

  def roadNetworkToJsonString(network: RoadNetwork): String = {
    val cities: Set[City] = network.cities.values.toSet

    val roads = cities.flatMap {
      _.adjacent.values
    } filter { road => road.orig < road.dest }

    val cityJsonEntries = cities map { city => CityJsonEntry(city.id, city.price) }

    val alliances = network.alliances

    val json =
      ("cities" -> Extraction.decompose(cityJsonEntries)) ~
        ("roads" -> Extraction.decompose(roads)) ~
        ("alliances" -> alliances) ~
        ("costPerMinute" -> network.travelCostPerMinute)

    pretty(render(json))
  }
}
