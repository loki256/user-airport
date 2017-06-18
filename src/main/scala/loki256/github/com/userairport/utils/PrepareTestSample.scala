package loki256.github.com.userairport.utils

import org.rogach.scallop.{ScallopConf, ScallopOption}

import scala.io.Source

/**
  * Small utility for preparing test sets
  */
object PrepareTestSample {

  class AppConf(arguments: Seq[String]) extends ScallopConf(arguments) {
    val input: ScallopOption[String] = opt[String](required = true)
    val result: ScallopOption[String] = opt[String](required = true)
    val size: ScallopOption[Int] = opt[Int](default = Some(1000))
    verify()
  }

  def main(args: Array[String]): Unit = {
    val appConf = new AppConf(args)
    val input = Source.fromFile(appConf.input()).getLines
    val header = input.take(1).map(x => s"$x\n").toVector
    val inputLines = input.toVector
    val sample = scala.util.Random.shuffle(inputLines).take(appConf.size())
    IOUtils.writeStringsToFile(
      header.toIterator ++ sample.toIterator.map(x => s"$x\n"),
      appConf.result()
    )
  }
}
