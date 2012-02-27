import sbt._
import Keys._

object ExplorerBuild extends Build {
  lazy val root = Project(id = "explorer",
    base = file(".")) aggregate(common, fetcher, processor)

  lazy val fetcher = Project(id = "fetcher",
    base = file("fetcher")) dependsOn(common)

  lazy val processor = Project(id = "processor",
    base = file("processor")) dependsOn(common)

  lazy val common = Project(id = "common",
    base = file("common"))
}

