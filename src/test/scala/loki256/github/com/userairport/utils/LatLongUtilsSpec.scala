package loki256.github.com.userairport.utils

import org.scalatest.{FunSpec, Matchers}

class LatLongUtilsSpec extends FunSpec with Matchers {

  import LatLongUtils._

  describe("distance") {
    it("should give zero distance for the same coordinates") {
      distance(0.2, 0.2, 0.2, 0.2) should be(0.0)
    }
    it("should give about earth radius * pi for distance between poles") {
      distance(90.0, 0.0, -90.0, 0.0) should be(3.1428 * 6371000.0 +- 10000)
    }
    it("should give correct value for predefined values") {
      distance(49.895278, 10.424722, 51.983611, 13.576667) should be(320407.05881035916)
    }
  }
}

