package loki256.github.com.userairport.algorithms

import com.github.davidmoten.grumpy.core.Position
import com.github.davidmoten.rtree.RTree
import com.github.davidmoten.rtree.geometry.{Geometries, Point, Rectangle}
import com.typesafe.scalalogging.LazyLogging
import loki256.github.com.userairport.model.{AirPort, User, UserAirportResult}
import rx.lang.scala.JavaConversions.toScalaObservable

import scala.annotation.tailrec


object RTreeImpl extends LazyLogging {

  @tailrec
  def createTree(input: List[AirPort], tree: RTree[String, Point]): RTree[String, Point] = {
    input match {
      case Nil => tree
      case x :: tail =>
        createTree(tail, tree.add(x.id, Geometries.point(x.lat, x.lon)))
    }
  }

  private def createBounds(from: Position, distanceKm: Double): Rectangle = {
    // this calculates a pretty accurate bounding box. Depending on the
    // performance you require you wouldn't have to be this accurate because
    // accuracy is enforced later
    val north = from.predict(distanceKm, 0)
    val south = from.predict(distanceKm, 180)
    val east = from.predict(distanceKm, 90)
    val west = from.predict(distanceKm, 270)

    Geometries.rectangle(west.getLon, south.getLat, east.getLon, north.getLat)
  }


  // avg complexity is O(NLog(M))
  def calculate(usersIterator: Iterator[User], airportsIterator: Iterator[AirPort]): Iterator[UserAirportResult] = {
    val tree = createTree(airportsIterator.toList, RTree.create())
    usersIterator.map { user =>
      val point = Geometries.point(user.lat, user.lon)
      val position = Position.create(point.y(), point.x())
      val bounds = createBounds(position, 500.0)
      val entities = toScalaObservable(tree.search(bounds)).toBlocking.toList
      if (entities.nonEmpty) {
        logger.info(s"Get ${entities.size} records")
        val ent = entities.head
        UserAirportResult(ent.value, user.uuid)
      } else {
        throw new RuntimeException("No entities")
      }
    }
  }

}
