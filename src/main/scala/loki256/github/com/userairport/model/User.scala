package loki256.github.com.userairport.model

case class User(uuid: String, lat: Double, lon: Double) extends LocationTrait

object User {

  // parse from string
  def apply(line: String): User = {
    val arr = line.split(",")
    require(arr.length == 3)
    User(arr(0), arr(1).toDouble, arr(2).toDouble)
  }

}
