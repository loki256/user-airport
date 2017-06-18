package loki256.github.com.userairport.model

case class AirPort(id: String, lat: Double, lon: Double) extends LocationTrait

object AirPort {

  // parse from string
  def apply(line: String): AirPort = {
    val arr = line.split(",")
    require(arr.length == 3)
    AirPort(arr(0), arr(1).toDouble, arr(2).toDouble)
  }

}
