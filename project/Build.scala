import sbt._
import Keys._

object ExplorerBuild extends Build {
  lazy val root = Project(id = "explorer",
    base = file(".")
  ) aggregate(common, fetcher, processor)

lazy val fetcher = Project(id = "fetcher",
  base = file("fetcher"),
  settings = Defaults.defaultSettings ++ Seq((unmanagedClasspath in Compile) += Attributed.blank(file("fetcher/src/main/resources")))
) dependsOn(common)

  lazy val processor = Project(id = "processor",
    base = file("processor"),
    settings = Defaults.defaultSettings ++ Seq((unmanagedClasspath in Compile) += Attributed.blank(file("processor/src/main/resources")))
  ) dependsOn(common)

  lazy val common = Project(id = "common",
    base = file("common"),
    settings = Defaults.defaultSettings ++ Seq((unmanagedClasspath in Compile) += Attributed.blank(file("common/src/main/resources")))
  )
}
