package loki256.github.com.userairport

import com.typesafe.scalalogging.StrictLogging
import loki256.github.com.userairport.model.{AirPort, User, UserAirportResult}
import loki256.github.com.userairport.utils.IOUtils
import org.rogach.scallop.{ScallopConf, ScallopOption}

import scala.io.Source

object Main extends StrictLogging {

  class AppConf(arguments: Seq[String]) extends ScallopConf(arguments) {
    val userFile: ScallopOption[String] = opt[String](required = true)
    val airportFile: ScallopOption[String] = opt[String](required = true)
    val resultFile: ScallopOption[String] = opt[String](required = true)
    verify()
  }

  def main(args: Array[String]): Unit = {
    val appConf = new AppConf(args)

    val airPorts: Iterator[AirPort] = Source.fromFile(appConf.airportFile()).getLines.drop(1).map { line =>
      AirPort(line)
    }

    val users: Iterator[User] = Source.fromFile(appConf.userFile()).getLines.drop(1).map { line =>
      User(line)
    }

    val geohashResult = algorithms.GeoHashMap.calculate(
      users,
      airPorts
    ).map { res: UserAirportResult =>
      s"${res.airport},${res.uuid}"
    }

    IOUtils.writeStringsToFile(
      geohashResult,
      "geohash.csv"
    )
  }
}
