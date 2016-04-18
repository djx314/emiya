import org.xarcher.sbt.CustomSettings
import org.xarcher.sbt.Dependencies
import org.xarcher.sbt.Helpers._

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

val gitInit = taskKey[String]("miao")

lazy val emiya = (project in file("."))
.settings(
  name := "emiya",
  version := "0.0.3"
).settings(

  libraryDependencies ++= Dependencies.ammoniteRepl,
  libraryDependencies += "net.coobird" % "thumbnailator" % "0.4.8",
  libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.60-R9",
  fork := true,

  {
    if (org.xarcher.sbt.OSName.isWindows)
      initialCommands in (Test, console) += s"""ammonite.repl.Main.run("repl.frontEnd() = ammonite.repl.frontend.FrontEnd.JLineWindows");"""
    else if (org.xarcher.sbt.OSName.isLinux)
      initialCommands in (Test, console) += s"""ammonite.repl.Main.run("");"""
    else
      initialCommands in (Test, console) += s""""""
  },

  gitInit := {

    val runtime = java.lang.Runtime.getRuntime

    import scala.io.Source
    if (org.xarcher.sbt.OSName.isWindows) {
      val commandLine = Source.fromFile("./gitUpdate").getLines.map(s => s.replaceAll("\\r\\n", "")).mkString(" & ")
      val process = runtime.exec(List("cmd", "/c", commandLine).toArray)
      execCommonLine(process)
    } else {
      val commandLine = Source.fromFile("./gitUpdate").getLines.map(s => s.replaceAll("\\r\\n", "")).mkString(" ; ")
      val process = runtime.exec(List("sh", "-c", commandLine).toArray)
      execCommonLine(process)
    }
    "执行 git 初始化操作成功"

  }

)
.settings(CustomSettings.customSettings: _*)
.enablePlugins(JDKPackagerPlugin)
.enablePlugins(WindowsPlugin)