package multiple

import CaseMap
import Obstacle
import Point
import TimePoint
import java.io.File
import java.lang.Integer.min

data class MultiAgentCase(
    val map: CaseMap,
    val startFinishPoints: List<Pair<Point, Point>>,
    val obstacles: List<Obstacle>
) {

    companion object {
        fun fromFile(mapFile: String, pointsFile: String, obstaclesFile: String): MultiAgentCase {
            val obstacles = File(obstaclesFile).readLines().chunked(3).map { obsRepr ->
                val xs = obsRepr[1].split(" ").map {it.toInt()}
                val ys = obsRepr[2].split(" ").map { it.toInt() }
                Obstacle(xs.zip(ys).mapIndexed { i, p -> TimePoint(Point(p.second, p.first), i.toLong()) }, 0.5)
            }

            val points = File(pointsFile).readLines().chunked(3).map { pointsStr ->
                val xs = pointsStr[1].split(" ").map {it.toInt()}
                val ys = pointsStr[2].split(" ").map { it.toInt() }
                Pair(Point(xs[1], xs[0]), Point(ys[1], ys[0]))
            }

            val realObs = obstacles.filter {obs -> points.all {(a, b) -> obs.points.first().getPoint() != a && obs.points.first().getPoint() != b && obs.points.last().getPoint() != a && obs.points.last().getPoint() != b}}

            return MultiAgentCase(
                CaseMap.fromFile(mapFile),
                points,
                realObs
            )
        }
    }

    fun emulate(paths: List<List<TimePoint>>) {
        getEmulation(paths).forEach {
            println(it)
            println()
        }
    }

    fun getEmulation(paths: List<List<TimePoint>>): List<String> {
        val maxLength = paths.maxOf {it.size}
        return (0 until min(maxLength, 100)).map { i ->
            val points = mutableListOf<Point>()
            paths.forEach {path ->
                if (path.size > i) {
                    points.add(path[i].getPoint())
                } else {
                    points.add(path.last().getPoint())
                }
            }
            getMapAtPathPoints(points, i.toLong())
        }
    }

    private fun getMapAtPathPoints(pathPoints: List<Point>, time: Long): String {
        val resLines = getMapLines()

        pathPoints.forEach { pathPoint ->
            resLines[pathPoint.y][pathPoint.x] = if (map.isFree(pathPoint)) 2 else 3
        }
        obstacles.forEachIndexed {io, obs ->
            obs.points.forEachIndexed { jo, op ->
                if (op.time == time || op.time < time && jo == obs.points.lastIndex) {
                    resLines[op.y][op.x] = io + 4
                }
            }
        }
        return getStrFromLines(resLines)
    }

    fun emulateToFile(paths: List<List<TimePoint>>, filePath: String) {
        val f = File(filePath)
        f.writeText("")
        getEmulation(paths).forEach {
            f.appendText(it)
            f.appendText("\n\n\n");
        }
    }

    private fun getStrFromLines(lines: Array<Array<Int>>): String {
        val res = lines.joinToString("") { it.joinToString("") { intIt ->
            when (intIt) {
                0 -> "."
                1 -> "#"
                2 -> "*"
                3 -> "*!"
                else -> "@"
            }
        } + "\n" }
        return res
    }

    private fun getMapLines(): Array<Array<Int>> {
        val mapLines = map.getGrid()

        val resLines = Array(map.h) { Array(map.w) { 0 } }
        for (i in 0 until map.h) {
            for (j in 0 until map.w) {
                resLines[i][j] = if (mapLines[i][j]) 0 else 1
            }
        }
        return resLines
    }
}