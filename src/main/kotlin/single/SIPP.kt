package single

import Point
import TimePoint
import java.util.*
import kotlin.collections.HashMap
import Timer

class SIPP: PathFindingAlgo {
    override fun findPath(case: SingleBotCase): PathFindingAlgo.AlgoRes {
        Timer.start()
        val timelines = Array(case.map.h) {Array(case.map.w) { mutableListOf<Pair<Long, Long>>() } }
        var cntOpen = 0
        var cntClose = 0

        //finding "bad" intervals for configurations
        case.obstacles.forEach { obs ->
            obs.points.forEachIndexed { i, op ->
                if (i != obs.points.lastIndex) {
                    if (i != 0 && obs.points[i].getPoint() != obs.points[i - 1].getPoint()) {
                        timelines[op.y][op.x].add(Pair(op.time - 1, op.time + 2))
                    } else {
                        timelines[op.y][op.x].add(Pair(op.time, op.time + 2))
                    }
                } else {
                    timelines[op.y][op.x].add(Pair(op.time - 1, Long.MAX_VALUE))
                }
            }
        }

        for (i in 0 until case.map.h) {
            for (j in 0 until case.map.w) {
                timelines[i][j] = toSafeTimeline(timelines[i][j].sortedBy { it.first }).toMutableList()
            }
        }

        if (timelines[case.startPoint.y][case.startPoint.x].isNotEmpty()) {
            if (timelines[case.startPoint.y][case.startPoint.x].first().first != 0L) {
                return PathFindingAlgo.AlgoRes(null, 0, 0, Timer.get())
            }
        }

        val open = TreeSet<TimeState> { a, b ->
            if (a.state == b.state) {
                0
            } else {
                if (a.f == b.f) {
                    a.hashCode().compareTo(b.hashCode())
                } else {
                    a.f.compareTo(b.f)
                }
            }
        }

        val stateToTime = HashMap<State, Long>()
        val prevState = HashMap<TimeState, TimeState>()
        val fs = State(case.startPoint, 0)
        stateToTime.put(fs, 0)

        open.add(TimeState(fs, 0L, getH(case.startPoint, case)))

        var finalState: TimeState? = null

        while (open.isNotEmpty()) {
            val v = open.pollFirst()!!
            cntClose += 1

            if (v.state.point == case.endPoint && timelines[v.state.point.y][v.state.point.x][v.state.intervalIndex].second >= 1e9) {
                finalState = v
                break
            }

            val neighbours = getNeighbours(v, case, timelines)
            neighbours.forEach { nb ->
                val oldTime = stateToTime.getOrDefault(nb.state, 1e9.toLong())
                prevState[nb] = v
                if (oldTime > nb.time) {
                    if (open.contains(nb)) {
                        open.remove(nb)
                    } else {
                        cntOpen++
                    }
                    open.add(nb)
                    stateToTime[nb.state] = nb.time
                }
            }
        }

        return PathFindingAlgo.AlgoRes(finalState?.let { restorePath(it, prevState, timelines) }, cntClose, cntOpen, Timer.get())
    }

    private fun getNeighbours(ts: TimeState, case: SingleBotCase, timelines: Array<Array<MutableList<Pair<Long, Long>>>>): List<TimeState> {
        val neighbPoints = TimePoint(ts.state.point, ts.time).getNeighbours().filter { case.map.isFree(it.getPoint()) }

        val res = mutableListOf<TimeState>()

        neighbPoints.forEach { nb ->
            val startTime = nb.time
            val endTime = timelines[ts.state.point.y][ts.state.point.x][ts.state.intervalIndex].second + nb.time - ts.time
            timelines[nb.y][nb.x].forEachIndexed { ii, inter ->
                if (inter.first < endTime && inter.second > startTime) {
                    val quickestTime = kotlin.math.max(nb.time, inter.first)
                    res.add(TimeState(State(nb.getPoint(), ii), quickestTime, quickestTime + getH(nb.getPoint(), case)))
                }
            }
        }
        return res
    }

    private fun getH(point: Point, case: SingleBotCase): Long {
        return (case.endPoint.x - point.x).toLong() + (case.endPoint.y - point.y)
    }

    private fun toSafeTimeline(badTimeLine: List<Pair<Long, Long>>): List<Pair<Long, Long>> {
        val res = mutableListOf<Pair<Long, Long>>()

        var currTp = 0L
        for (badInterval in badTimeLine) {
            if (currTp < badInterval.first) {
                res.add(Pair(currTp, badInterval.first))
            }
            currTp = badInterval.second
        }
        res.add(Pair(currTp, 1e18.toLong()))
        return res
    }

    private fun restorePath(point: TimeState, prevs: HashMap<TimeState, TimeState>, timelines: Array<Array<MutableList<Pair<Long, Long>>>>): List<TimePoint> {
        fun restorePathInt(point: TimeState, prevs: HashMap<TimeState, TimeState>): List<TimeState> {
            return if (prevs.containsKey(point)) {
                val res = restorePathInt(prevs[point]!!, prevs).toMutableList();
                res.add(point)
                res
            } else {
                listOf(point)
            }
        }

        val timeStates = restorePathInt(point, prevs)

        val res = mutableListOf<TimePoint>()

        var currTime = 0L
        for (i in timeStates.indices) {
            val ts = timeStates[i]
            if (i != timeStates.lastIndex) {
                while (currTime <= timeStates[i + 1].time - 1) {
                    res.add(TimePoint(ts.state.point, currTime))
                    currTime += 1
                }
            } else {
                res.add(TimePoint(ts.state.point, ts.time))
            }
        }
        return res
    }

    private data class State(
        val point: Point,
        val intervalIndex: Int
    )

    private data class TimeState(
        val state: State,
        val time: Long,
        val f: Long
    )

}