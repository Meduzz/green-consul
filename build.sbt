name := "green-consul"

version := "20190530"

scalaVersion := "2.12.8"

organization := "se.chimps.green"

credentials += Credentials(Path.userHome / ".ivy2" / ".green")

publishTo := Some("se.chimps.green" at "https://yamr.kodiak.se/maven")

publishArtifact in (Compile, packageDoc) := false

resolvers += "se.chimps.green" at "https://yamr.kodiak.se/maven"

resolvers += "se.kodiak.tools" at "https://yamr.kodiak.se/maven"

libraryDependencies ++= Seq(
	"se.chimps.green" %% "green-spi" % "20190519",
	"se.kodiak.tools" %% "yahc" % "1.5",
	"org.scalatest" %% "scalatest" % "3.0.5" % "test"
)
