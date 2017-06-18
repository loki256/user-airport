package loki256.github.com.userairport.algorithms

import com.typesafe.scalalogging.LazyLogging
import loki256.github.com.userairport.model.{AirPort, User, UserAirportResult}
import loki256.github.com.userairport.utils.LatLongUtils

object BruteForce extends LazyLogging {

  // complexity O(N)
  def getNearestAirportToUser(user: User, airPorts: Seq[AirPort]): AirPort = {
    require { airPorts.nonEmpty }
    airPorts.reduce { (port1, port2) =>
      if (LatLongUtils.distance(port1.lat, port1.lon, user.lat, user.lon) <= LatLongUtils.distance(port2.lat, port2.lon, user.lat, user.lon)) {
        port1
      } else {
        port2
      }
    }
  }

  // complexity O(NxM), it's mainly for tests
  def calculate(usersIterator: Iterator[User], airportsIterator: Iterator[AirPort]): Iterator[UserAirportResult] = {
    val ports = airportsIterator.toArray
    logger.info(s"Get ${ports.length} airports")
    usersIterator.map { user =>
      val port = getNearestAirportToUser(user, ports)
      UserAirportResult(port.id, user.uuid)
    }
  }
}
