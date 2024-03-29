package sparky

import org.apache.spark.{SparkConf, SparkContext}


object SparkApp {

  def sparkJob() = {

    val file = "/etc/passwd"
    val conf = new SparkConf()
          .setAppName("Spark Template")
                 //Add more config if needed
          .setMaster("local[*]")

    val sc = new SparkContext(conf)

      val data = sc.textFile(file).cache

      val numAs =
        data.filter(line => line.contains("a")).count()

      val numBs =
        data.filter(line => line.contains("b")).count()

      val piped = data.pipe("grep a").collect();

      println("Lines with a: %s, Lines with b: %s".format(numAs, numBs))
    }

 
  def main(args: Array[String])= sparkJob

}
