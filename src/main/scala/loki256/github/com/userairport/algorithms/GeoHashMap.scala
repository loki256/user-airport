package loki256.github.com.userairport.algorithms

import ch.hsr.geohash.GeoHash
import com.typesafe.scalalogging.LazyLogging
import loki256.github.com.userairport.model.{AirPort, LocationTrait, User, UserAirportResult}
import loki256.github.com.userairport.utils.GeoUtils

import scala.annotation.tailrec
import scala.collection.mutable


object GeoHashMap extends LazyLogging {

  // this map contains geohash (u9 for example) -> sequence of all airports which belongs to this geohash
  type GeohashMap = Map[String, Seq[AirPort]]

  def getNeighboursGeohashStrings(lat: Double, lon: Double, geohashAccuracy: Int): Seq[String] = {
    // for the lat, lon return geohash + 8 surrounding geoHashes
    val gh = GeoHash.withCharacterPrecision(lat, lon, geohashAccuracy)
    val neighbours = gh.getAdjacent.map(_.toBase32)
    neighbours :+ gh.toBase32
  }

  def poiWithAdjustedToGeohashMap[T <: LocationTrait](pois: Seq[T], geohashAccuracy: Int, initMap: Map[String, Seq[T]]): Map[String, Seq[T]] = {
    logger.debug(s"Calculating GeohashMap for level $geohashAccuracy")
    // string geohash as a key and sequence with T itself as a value
    val result = pois.foldLeft(initMap) { (mp, poi) =>
      val updates = getNeighboursGeohashStrings(poi.lat, poi.lon, geohashAccuracy).map { hsh: String =>
        (hsh, mp.getOrElse(hsh, Seq()) :+ poi)
      }
      mp ++ updates
    }
    logger.debug(s"Result size: ${result.size}")
    result
  }

  // we start from the smallest (largest) geohash and decrease it (increase geographical size)
  // if no airports for such geohash
  @tailrec
  def getClosestAirportForUser(user: User, geohashSize: Int, getAirportsByGeohash: ((User, GeoHash) => Seq[AirPort])): Option[AirPort] = {
    if (geohashSize == 0) {
      None
    } else {
      val userGeoHash = GeoHash.withCharacterPrecision(user.lat, user.lon, geohashSize)
      val ports = getAirportsByGeohash(user, userGeoHash)
      if (ports.nonEmpty) {
        // now just use brute force
        Some(BruteForce.getNearestAirportToUser(user, ports))
      } else {
        getClosestAirportForUser(user, geohashSize - 1, getAirportsByGeohash)
      }
    }
  }

  def calculate(usersIterator: Iterator[User], airportsIterator: Iterator[AirPort]): Iterator[UserAirportResult] = {

    // collect to memory
    val ports = airportsIterator.toArray
    logger.debug(s"Calculating for ${ports.length} ports")

    // this cache contains GeohashMap for specific geohashAccuracy. We are going to fill it lazily
    // since we don't really know if we need to use smallest geohash accuracies (such as 2 or 1)
    // for example item with key 2 contains all Geohash dict for geohashAccuracy 2
    val geohashMapCache: mutable.Map[Int, GeohashMap] = mutable.Map[Int, GeohashMap]()

    val airportRetriever = (user: User, userGeohash: GeoHash) => {
      val geohashAccuracy: Int = userGeohash.getCharacterPrecision

      // we calculate geohashMap for accuracy only if this accuracy is required
      val mp = geohashMapCache.getOrElseUpdate(geohashAccuracy, poiWithAdjustedToGeohashMap(ports, geohashAccuracy, Map()))
      val result = mp.getOrElse(userGeohash.toBase32, Seq())

      // we can't return airports which are farther from user then
      // min(width, height) of user's geohash
      lazy val minUserGeohashDistance = GeoUtils.geohashMinDistance(userGeohash)
      result.filter { poi =>
        GeoUtils.distance(user, poi) < minUserGeohashDistance
      }
    }

    usersIterator.map { user =>
      val closestPort = getClosestAirportForUser(user, 5, airportRetriever).getOrElse(throw new RuntimeException(s"Logic error for user: $user"))
      UserAirportResult(closestPort.id, user.uuid)
    }
  }
}
