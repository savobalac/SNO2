name := "sno2"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache
)     

templatesImport += "com.avaje.ebean._"

play.Project.playJavaSettings
