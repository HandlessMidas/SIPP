package single

import Point
import TimePoint
import Timer
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.abs

class AStarWithTimeDimension: PathFindingAlgo {
    override fun findPath(case: SingleBotCase): PathFindingAlgo.AlgoRes {
        Timer.start()
        val open = TreeSet<AStarTimePoint> { a, b ->
            if (a.F == b.F) {
                if (a.timepoint.time == b.timepoint.time) {
                    if (a.timepoint.x == b.timepoint.x) {
                        a.timepoint.y.compareTo(b.timepoint.y)
                    } else {
                        a.timepoint.x.compareTo(b.timepoint.x)
                    }
                } else {
                    -a.timepoint.time.compareTo(b.timepoint.time)
                }
            } else {
                a.F.compareTo(b.F)
            }
        }
        var cntOpen = 0
        var cntClose = 0
        open.add(AStarTimePoint(TimePoint(case.startPoint, 0), getH(case.startPoint, case)))

        val usedPoints = HashSet<TimePoint>()
        usedPoints.add(TimePoint(case.startPoint, 0))

        val prevPoints = HashMap<TimePoint, TimePoint>()

        var finalPoint: TimePoint? = null

        var minFinishTime = 0L
        case.obstacles.forEach { obs ->
            obs.points.forEach { op ->
                if (op.getPoint() == case.endPoint) {
                    minFinishTime = op.time + 1
                }
            }
        }
        while (open.isNotEmpty()) {
            val currPoint = open.pollFirst()!!.timepoint
            cntClose += 1

            if (currPoint.x == case.endPoint.x && currPoint.y == case.endPoint.y && currPoint.time >= minFinishTime) {
                finalPoint = currPoint
                break
            }

            val neigs = getNeighbours(currPoint, case)
            neigs.forEach {
                if (!usedPoints.contains(it.timepoint)) {
                    cntOpen += 1
                    usedPoints.add(it.timepoint)
                    open.add(it)

                    prevPoints[it.timepoint] = currPoint
                }
            }
        }
        if (finalPoint == null) {
            return PathFindingAlgo.AlgoRes(null, cntClose, cntOpen, Timer.get())
        }

        return PathFindingAlgo.AlgoRes(restorePath(finalPoint, prevPoints), cntClose, cntOpen, Timer.get())
    }

    private fun getNeighbours(point: TimePoint, case: SingleBotCase): List<AStarTimePoint> {
        return point.getNeighbours().filter { case.isCorrectTransition(point, it) }.map { AStarTimePoint(it, it.time + getH(it.getPoint(), case)) }
    }

    private fun restorePath(point: TimePoint, prevs: HashMap<TimePoint, TimePoint>): List<TimePoint> {
        fun restorePathInt(point: TimePoint, prevs: HashMap<TimePoint, TimePoint>): List<TimePoint> {
            return if (prevs.containsKey(point)) {
                val res = restorePathInt(prevs[point]!!, prevs).toMutableList();
                res.add(point)
                res
            } else {
                listOf(point)
            }
        }

        return restorePathInt(point, prevs)
    }

    private fun getH(point: Point, case: SingleBotCase): Long {
        return abs(case.endPoint.x - point.x).toLong() + abs(case.endPoint.y - point.y)
    }

    private data class AStarTimePoint(
        val timepoint: TimePoint,
        val F: Long
    )
}