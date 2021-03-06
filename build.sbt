name := "temperature-monitor"

organization := "pdorobisz"

version := "1.0"

scalaVersion := "2.12.3"

lazy val root = (project in file(".")).enablePlugins(PlayScala, JavaAppPackaging, JDebPackaging, SystemdPlugin)

libraryDependencies += guice

libraryDependencies += "com.paulgoldbaum" %% "scala-influxdb-client" % "0.5.2"

sources in(Compile, doc) := Seq.empty

publishArtifact in(Compile, packageDoc) := false

maintainer in Linux := "Piotr Dorobisz"

packageSummary in Linux := "Temperature and humidity monitor"

packageDescription := "Temperature and humidity monitor"

// These settings will go to conf/application.ini file.
javaOptions in Universal ++= Seq(
  // JVM memory tuning
  //  "-J-Xmx1024m",
  //  "-J-Xms512m",

  s"-Dconfig.file=/usr/share/${packageName.value}/conf/production.conf",
  s"-Dapp.home=/usr/share/${packageName.value}",
  s"-Dlogdir=/var/log/${packageName.value}"
)

linuxPackageMappings += {
  val file = sourceDirectory.value / "debian" / "package" / "bin"
  packageDirectoryAndContentsMapping((file, s"/usr/share/${packageName.value}/bin")).withPerms("755")
}

linuxPackageMappings += {
  val file = sourceDirectory.value / "debian" / "package" / "conf"
  packageDirectoryAndContentsMapping((file, s"/usr/share/${packageName.value}/conf")).withPerms("644")
}

mappings in Universal := (mappings in Universal).value.filter { case (_, fileName) =>
  !fileName.endsWith(".md")
}
