name := "Explorer-Processor"

version := "1.0"

scalaVersion := "2.9.1"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Typesafe Repository (Snapshots)" at "http://repo.typesafe.com/typesafe/snapshots/"

resolvers += "fyrie snapshots" at "http://repo.fyrie.net/snapshots"

resolvers ++= Seq("snapshots" at "http://scala-tools.org/repo-snapshots",
    "releases"  at "http://scala-tools.org/repo-releases")

libraryDependencies ++= Seq(
    "com.typesafe.akka" % "akka-actor" % "2.0-RC1",
    "com.typesafe.akka" % "akka-amqp" % "2.0-SNAPSHOT",
    "com.typesafe.akka" % "akka-testkit" % "2.0-RC1",
    "net.fyrie" %% "fyrie-redis" % "1.2-SNAPSHOT",
    "org.jsoup" % "jsoup" % "1.6.1",
    "com.ning" % "async-http-client" % "1.6.4" % "compile",
    "org.slf4j" % "slf4j-nop" % "1.6.0" % "runtime",
    "junit" % "junit" % "4.7"
    )
