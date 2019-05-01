
name := "cexshop"

version := "1.1.0"

// must be at least 2.11 to use hmt_textmodel
scalaVersion := "2.12.4"

run / connectInput := true

resolvers += Resolver.jcenterRepo
resolvers += Resolver.bintrayRepo("neelsmith","maven")
resolvers += Resolver.bintrayRepo("eumaeus", "maven")

val circeVersion = "0.10.0"

libraryDependencies ++=   Seq(
  "edu.holycross.shot.cite" %% "xcite" % "4.0.2",
  "edu.holycross.shot" %% "ohco2" % "10.12.5",
  "edu.holycross.shot" %% "orca" % "4.2.0",
  "edu.holycross.shot" %% "scm" % "6.2.2",
  "edu.holycross.shot" %% "greek" % "2.3.3",
  "edu.holycross.shot" %% "citeobj" % "7.3.2",
  "edu.holycross.shot" %% "gsphone" % "1.1.0",
  "edu.furman.classics" %% "citealign" % "0.5.0",
  "edu.furman.classics" %% "fumorph" % "0.12.0",
  "org.homermultitext" %% "hmt-textmodel" % "4.0.0",
  "edu.furman.classics" %% "citewriter" % "1.0.1"
)

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-optics",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

