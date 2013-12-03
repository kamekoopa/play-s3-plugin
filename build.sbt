name := """play-s3-plugin"""

organization := "net.kamekoopa"

version := "0.1"

scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
    "com.amazonaws"     %  "aws-java-sdk" % "1.6.7",
    "com.typesafe.play" %% "play"         % "2.2.1" % "provided",
    "com.typesafe.play" %% "play-test"    % "2.2.1" % "test"
)

parallelExecution in Test := false
