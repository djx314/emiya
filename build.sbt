import org.xarcher.sbt.CustomSettings

import sbt._
import sbt.Keys._

val printlnDo = println("""
|                                                     _   _         _
|                                                    | | (_)       | |
|  ___   _ __    _   _   _ __ ___     __ _      ___  | |  _   ___  | |__
| / _ \ | '_ \  | | | | | '_ ` _ \   / _` |    / _ \ | | | | / __| | '_ \
||  __/ | | | | | |_| | | | | | | | | (_| |   |  __/ | | | | \__ \ | | | |
| \___| |_| |_|  \__,_| |_| |_| |_|  \__,_|    \___| |_| |_| |___/ |_| |_|
""".stripMargin
)

libraryDependencies ++= {
  // Determine OS version of JavaFX binaries
  lazy val osName = System.getProperty("os.name") match {
    case n if n.startsWith("Linux") => "linux"
    case n if n.startsWith("Mac") => "mac"
    case n if n.startsWith("Windows") => "win"
    case _ => throw new Exception("Unknown platform!")
  }
  Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
    .map(m => "org.openjfx" % s"javafx-$m" % "11" classifier osName)
}

lazy val emiya = (project in file("."))
.settings(
  name := "emiya",
  version := "0.0.3"
).settings(

  //libraryDependencies ++= Dependencies.ammoniteRepl,
  libraryDependencies += "net.coobird" % "thumbnailator" % "0.4.17",
  libraryDependencies += "org.scalafx" %% "scalafx" % "18.0.2-R29",
  fork := true

)
.settings(CustomSettings.customSettings: _*)
.enablePlugins(JDKPackagerPlugin)
.enablePlugins(WindowsPlugin)
