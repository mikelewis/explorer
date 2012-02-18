import sbt._
import Keys._

object ExplorerBuild extends Build {
  lazy val root = Project(id = "explorer",
    base = file(".")) aggregate(fetcher)

  lazy val fetcher = Project(id = "explorer-fetcher",
    base = file("fetcher"))

  //lazy val processor = Project(id = "explorer-processor",
  //  base = file("processor"))
}

