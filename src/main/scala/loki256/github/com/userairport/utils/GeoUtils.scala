package loki256.github.com.userairport.utils

import Math._

import ch.hsr.geohash.GeoHash
import loki256.github.com.userairport.model.LocationTrait

object GeoUtils {

  val radius = 6371000 // meters

  def distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double = {
    val dLat = toRadians(lat2 - lat1)
    val dLon = toRadians(lon2 - lon1)
    val a = pow(sin(dLat / 2), 2) + cos(toRadians(lat1)) * cos(toRadians(lat2)) * pow(sin(dLon / 2), 2)
    val c = 2 * asin(sqrt(a))
    radius * c
  }

  def distance(l1: LocationTrait, l2: LocationTrait): Double = {
    distance(l1.lat, l1.lon, l2.lat, l2.lon)
  }

  /**
    * Returns minimum between width and height of geohash in meters
    * @param geoHash GeoHash
    * @return
    *
    * geohash is not a rectangle
    */
  def geohashMinDistance(geoHash: GeoHash): Double = {
    val box = geoHash.getBoundingBox
    Seq(
      distance(box.getMaxLat, box.getMinLon, box.getMaxLat, box.getMaxLon),
      distance(box.getMinLat, box.getMinLon, box.getMinLat, box.getMaxLon),
      distance(box.getMaxLat, box.getMaxLon, box.getMinLat, box.getMaxLon),
      distance(box.getMaxLat, box.getMinLon, box.getMinLat, box.getMinLon)
    ).min
  }
}
