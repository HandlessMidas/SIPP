package single

import TimePoint
import Timer
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class DijkstraWithTimeDimension: PathFindingAlgo {
    override fun findPath(case: SingleBotCase): PathFindingAlgo.AlgoRes {
        Timer.start()
        val open = TreeSet<TimePoint> { a, b ->
            if (a.time == b.time) {
                if (a.x == b.x) {
                    a.y.compareTo(b.y)
                } else {
                    a.x.compareTo(b.x)
                }
            } else {
                a.time.compareTo(b.time)
            }
        }
        var cntOpen = 0
        var cntClose = 0
        open.add(TimePoint(case.startPoint, 0))

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
            val currPoint = open.pollFirst()!!
            cntClose += 1

            if (currPoint.x == case.endPoint.x && currPoint.y == case.endPoint.y && currPoint.time >= minFinishTime) {
                finalPoint = currPoint
                break
            }

            val neigs = getNeighbours(currPoint, case)
            neigs.forEach {
                if (!usedPoints.contains(it)) {
                    cntOpen += 1
                    usedPoints.add(it)
                    open.add(it)

                    prevPoints[it] = currPoint
                }
            }
        }
        if (finalPoint == null) {
            return PathFindingAlgo.AlgoRes(null, cntClose, cntOpen, Timer.get())
        }

        return PathFindingAlgo.AlgoRes(restorePath(finalPoint, prevPoints), cntClose, cntOpen, Timer.get())
    }

    private fun getNeighbours(point: TimePoint, case: SingleBotCase): List<TimePoint> {
        return point.getNeighbours().filter { case.isCorrectTransition(point, it) }
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
}