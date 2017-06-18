package loki256.github.com.userairport

import com.typesafe.scalalogging.StrictLogging
import loki256.github.com.userairport.model.{AirPort, User, UserAirportResult}
import loki256.github.com.userairport.utils.IOUtils
import org.rogach.scallop.{ScallopConf, ScallopOption}
import com.github.tototoshi.csv.CSVReader

object Main extends StrictLogging {

  class AppConf(arguments: Seq[String]) extends ScallopConf(arguments) {
    val userFile: ScallopOption[String] = opt[String](required = true)
    val airportFile: ScallopOption[String] = opt[String](required = true)
    val resultFile: ScallopOption[String] = opt[String](default = Some("result.cvs"))
    verify()
  }

  def main(args: Array[String]): Unit = {
    val appConf = new AppConf(args)

    val airPorts: Iterator[AirPort] = CSVReader.open(appConf.airportFile()).iteratorWithHeaders.map { x =>
      AirPort(x("iata_code"), x("latitude").toDouble, x("longitude").toDouble)
    }

    val users: Iterator[User] = CSVReader.open(appConf.userFile()).iteratorWithHeaders.map { x =>
      User(x("uuid"), x("geoip_latitude").toDouble, x("geoip_longitude").toDouble)
    }

    val geohashResult = algorithms.BruteForce.calculate(
      users,
      airPorts
    ).map { res: UserAirportResult =>
      s"${res.airport},${res.uuid}\n"
    }

    IOUtils.writeStringsToFile(
      geohashResult,
      appConf.resultFile()
    )
  }
}
