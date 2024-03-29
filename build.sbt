
// Project name
name := "Spark Template"

// Don't forget to set the version
version := "0.1.-SNAPSHOT"

// All Spark Packages need a license
licenses := Seq("Apache-2.0" -> url("http://opensource.org/licenses/Apache-2.0"))

// scala version to be used
scalaVersion := "2.10.5"

// force scalaVersion
//ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

// spark version to be used
val sparkVersion = "1.4.1"

// Needed as SBT's classloader doesn't work well with Spark
fork := true

// Java version
javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

// add a JVM option to use when forking a JVM for 'run'
javaOptions ++= Seq("-Xmx2G")


// Use local repositories by default
resolvers ++= Seq(
  Resolver.defaultLocal,
  Resolver.mavenLocal,
  // make sure default maven local repository is added... Resolver.mavenLocal has bugs.
  "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
  // For Typesafe goodies, if not available through maven
  // "Typesafe" at "http://repo.typesafe.com/typesafe/releases",
  // For Spark development versions, if you don't want to build spark yourself
  "Apache Staging" at "https://repository.apache.org/content/repositories/staging/"
)


/// Dependencies

// copy all dependencies into lib_managed/
retrieveManaged := true

// scala modules (should be included by spark, just an exmaple)
//libraryDependencies ++= Seq(
//  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
//  "org.scala-lang" % "scala-compiler" % scalaVersion.value
//  )

val sparkDependencyScope = "provided"

// spark modules (should be included by spark-sql, just an example)
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % sparkDependencyScope,
  "org.apache.spark" %% "spark-sql" % sparkVersion % sparkDependencyScope,
  "org.apache.spark" %% "spark-mllib" % sparkVersion % sparkDependencyScope,
  "org.apache.spark" %% "spark-streaming" % sparkVersion % sparkDependencyScope
)

// logging
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"

// testing
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.12.2" % "test"



/// console

// define the statements initially evaluated when entering 'console', 'consoleQuick', or 'consoleProject'
// but still keep the console settings in the sbt-spark-package plugin

// If you want to use yarn-client for spark cluster mode, override the environment variable
// SPARK_MODE=yarn-client <cmd>
val sparkMode = sys.env.getOrElse("SPARK_MODE", "local[2]")


initialCommands in console :=
  s"""
     |import org.apache.spark.SparkConf
     |import org.apache.spark.SparkContext
     |import org.apache.spark.SparkContext._
     |
     |@transient val sc = new SparkContext(
     |  new SparkConf()
     |    .setMaster("$sparkMode")
                                  |    .setAppName("Console test"))
                                  |implicit def sparkContext = sc
                                  |import sc._
                                  |
                                  |@transient val sqlc = new org.apache.spark.sql.SQLContext(sc)
                                  |implicit def sqlContext = sqlc
                                  |import sqlc._
                                  |
                                  |def time[T](f: => T): T = {
                                  |  import System.{currentTimeMillis => now}
                                  |  val start = now
                                  |  try { f } finally { println("Elapsed: " + (now - start)/1000.0 + " s") }
                                  |}
                                  |
                                  |""".stripMargin

cleanupCommands in console :=
  s"""
     |sc.stop()
   """.stripMargin


