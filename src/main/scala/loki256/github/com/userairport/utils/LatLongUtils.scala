package loki256.github.com.userairport.utils

import Math._

object LatLongUtils {

  val radius = 6371000 // meters

  def distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double = {
    val dLat = toRadians(lat2 - lat1)
    val dLon = toRadians(lon2 - lon1)
    val a = pow(sin(dLat / 2), 2) + cos(toRadians(lat1)) * cos(toRadians(lat2)) * pow(sin(dLon / 2), 2)
    val c = 2 * asin(sqrt(a))
    radius * c
  }


}
