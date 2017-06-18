package loki256.github.com.userairport.algorithms

import loki256.github.com.userairport.model.{AirPort, LocationTrait, User}
import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSpec, Matchers}

import scala.io.Source

class GeoHashMapSpec extends FunSpec with Matchers with GeneratorDrivenPropertyChecks {
  import GeoHashMap._

  describe("getNeighboursGeohashStrings") {

    it("should return 9 geoHashes for coordinates") {
      val latGen = Gen.choose[Double](0.0, 90.0)
      val lonGen = Gen.choose[Double](-180.0, 180.0)
      val geohashAccGen = Gen.choose(1, 12)
      forAll(latGen, lonGen, geohashAccGen) { (lat: Double, lon: Double, geohashAccuracy: Int) =>
        getNeighboursGeohashStrings(lat, lon, geohashAccuracy).length should be (9)
      }
    }
  }

  describe("poiWithAdjustedToGeohashMap") {
    case class Loc(lat: Double, lon: Double) extends LocationTrait

    it("should fill map with geohash of locations with neighbours") {
      val locations = Seq(
        Loc(0, 0),
        Loc(50.0, 50.0),
        Loc(90, 120.0)
      )
      val result = poiWithAdjustedToGeohashMap(locations, 5, Map())
      result.size should be (locations.size * 9)
    }

    it("should reuse the same geohash for close location") {
      val locations = Seq(
        Loc(20.0001, 20.1),
        Loc(20.0002, 20.1),
        Loc(50, 50)
      )

      val result = poiWithAdjustedToGeohashMap(locations, 4, Map())
      result.size should be (9 + 9)
    }
  }

  describe("calculate") {
    it("should give the same result as brute force") {
      val airPorts = Source.fromInputStream(getClass.getResourceAsStream("/airports.csv")).getLines.drop(1).map { line =>
        AirPort(line)
      }.toSeq

      val users = Source.fromInputStream(getClass.getResourceAsStream("/users_sample.csv")).getLines.drop(1).map { line =>
        User(line)
      }.take(45).toSeq

      val bruteForceResult = BruteForce.calculate(users.toIterator, airPorts.toIterator).toSeq
      val geohashMapResult = GeoHashMap.calculate(users.toIterator, airPorts.toIterator).toSeq

      bruteForceResult should be (geohashMapResult)
    }

  }

}
