name := "BlockchainTransactionsAPP"

version := "0.1"

scalaVersion := "2.11.12"
mainClass := Some("RunWebSocket")

val sparkVersion = "2.3.4"



libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.26"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.26"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.11"
libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "2.0.2"
