import java.io.File

class CaseMap(strMap: String) {
    private val map: Array<Array<Boolean>>
    val h: Int
    val w: Int

    init {
        val lines = strMap.lines()
        h = lines.size
        w = lines[0].length
        map = Array(h) { Array(w) { false } }

        for (i in 0 until h) {
            for (j in 0 until w) {
                map[i][j] = lines[i][j] == '.'
            }
        }
    }

    fun isFree(point: Point): Boolean {
        if (point.x < 0 || point.y < 0 || point.x >= w || point.y >= h) {
            return false
        }
        return map[point.y][point.x]
    }

    fun getGrid(): Array<Array<Boolean>> {
        return map
    }

    companion object {
        fun fromFile(fileName: String): CaseMap {
            return CaseMap(File(fileName).readLines().drop(4).joinToString("\n"))
        }
    }
}

data class Point(val x: Int, val y: Int) {
    fun dist2(other: Point): Long {
        return (x - other.x) * (x - other.x).toLong() + (y - other.y) * (y - other.y)
    }
}

data class TimePoint(val x: Int, val y: Int, val time: Long) {
    constructor(point: Point, time: Long): this(point.x, point.y, time)

    fun getNeighbours(): List<TimePoint> {
        return listOf(Pair(-1, 0), Pair(1, 0), Pair(0, 0), Pair(0, -1), Pair(0, 1)).map { (i, j) ->
            TimePoint(x + i, y + j, time + 1)
        }
    }

    fun getPoint(): Point {
        return Point(x, y)
    }
}

data class Obstacle(val points: List<TimePoint>, val R: Double)