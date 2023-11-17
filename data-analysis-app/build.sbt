ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.12"

val sparkVersion = "3.5.0"

lazy val root = (project in file("."))
  .settings(
    name := "data-analysis-app",
    libraryDependencies ++= Seq(
      "org.postgresql" % "postgresql" % "42.5.4",
      "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
      "org.apache.spark" %% "spark-sql" % sparkVersion % "provided"
    )
  )

