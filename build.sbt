name := "AAL"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.json4s" %% "json4s-native" % "3.3.0",
  "net.sf.jung" % "jung-visualization" % "2.0.1",
  "net.sf.jung" % "jung-graph-impl" % "2.0.1",
  "org.scalatest" %% "scalatest" % "3.0.0-M15",
  "com.github.scopt" %% "scopt" % "3.4.0"
)
    