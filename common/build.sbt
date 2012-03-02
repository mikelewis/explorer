name := "common"

version := "1.0"

scalaVersion := "2.9.1"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Akka Snapshot Repo" at "http://akka.io/snapshots"

resolvers ++= Seq("snapshots" at "http://scala-tools.org/repo-snapshots",
    "releases"  at "http://scala-tools.org/repo-releases")

resolvers += "fyrie snapshots" at "http://repo.fyrie.net/snapshots"

libraryDependencies += "com.typesafe.akka" % "akka-actor" % "2.0-20120124-000638"

libraryDependencies += "com.typesafe.akka" % "akka-testkit" % "2.0-20120124-000638" % "test"

libraryDependencies ++= Seq(
    "net.fyrie" %% "fyrie-redis" % "2.0-SNAPSHOT",
    "org.slf4j" % "slf4j-nop" % "1.6.0" % "runtime",
    "junit" % "junit" % "4.7"
    )
