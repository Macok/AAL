package com.mac.aal

import com.mac.aal.Mode.Mode

import java.io.File

/**
  * Created by mac on 08.06.16.
  */
case class Config(mode: Mode,
                  citiesCount: Int = 0,
                  roadsCount: Int = 0,
                  alliancesCount: Int = 0,
                  cityPriceMin: Int = 0,
                  cityPriceMax: Int = 50,
                  roadPriceMin: Int = 0,
                  roadPriceMax: Int = 50,
                  roadDurationMin: Int = 1,
                  roadDurationMax: Int = 50,
                  costPerMinute: Double = 0.5,
                  output: File = new File("output.json"),
                  input: File = new File(""),
                  cityStart: String = "city1",
                  cityEnd: String = "city2",
                  density: Double = 0.1,
                  testDimensionMin: Int = 100,
                  testDimensionMax: Int = 600,
                  testDimensionStep: Int = 15)
