import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "SNO2"
    val appVersion      = "1.0"

    val appDependencies = Seq(
        // Add your project dependencies here,
        "mysql" % "mysql-connector-java" % "5.1.21",
        "org.reflections" % "reflections" % "0.9.8",
        javaCore, javaJdbc, javaEbean,
        "com.amazonaws" % "aws-java-sdk" % "1.3.11",
        "com.jolbox" % "bonecp" % "0.8.0-rc2-SNAPSHOT-20130712-14382677.jar"
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      // Add your own project settings here
      testOptions in Test += Tests.Argument("junitxml", "console"),
      libraryDependencies += "com.jolbox" % "bonecp" % "0.8.0-rc2-SNAPSHOT-20130712-14382677.jar" from "https://dl.dropboxusercontent.com/u/36714110/libraries/bonecp-patches/bonecp-0.8.0-rc2-SNAPSHOT-20130712-14382677.jar"
    )

}
