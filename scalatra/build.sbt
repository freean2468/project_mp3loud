val ScalatraVersion = "2.7.1"

ThisBuild / scalaVersion := "2.13.4"
ThisBuild / organization := "com.mirae"

lazy val hello = (project in file("."))
  .settings(
    name := "mp3loud",
    version := "0.0.1-SNAPSHOT",
    libraryDependencies ++= Seq(
      "org.scalatra" %% "scalatra" % ScalatraVersion,
      "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
      "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
      //"org.eclipse.jetty" % "jetty-webapp" % "9.4.35.v20201120" % "compile;provided;container",
      "org.eclipse.jetty" % "jetty-webapp" % "9.4.35.v20201120" % "compile;provided;container",
      "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",

      // Database packages
      "org.postgresql" % "postgresql" % "42.2.5", //org.postgresql.ds.PGSimpleDataSource dependency
      "com.typesafe.slick" %% "slick" % "3.3.2",
//      "org.slf4j" % "slf4j-nop" % "1.6.4",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.3.2",

      // Json formatting packages
      "org.scalatra" %% "scalatra-json" % "2.7.0",
      "org.json4s" %% "json4s-jackson" % "3.6.11",

      // for configuration
      "com.typesafe" % "config" % "1.4.1",

      "org.scalatra" %% "scalatra-scalate" % "2.7.0"
    ),
  )

enablePlugins(SbtTwirl)
enablePlugins(JettyPlugin)
enablePlugins(JavaAppPackaging)
//enablePlugins(DockerPlugin)