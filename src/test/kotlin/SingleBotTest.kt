import org.junit.jupiter.api.Test
import single.DijkstraWithTimeDimension
import single.SIPP
import single.SingleBotCase
import java.lang.IllegalStateException
import java.lang.Math.abs

class SingleBotTest {
    private fun checkPath(case: SingleBotCase, path: List<TimePoint>) {
        for (i in path.indices) {
            if (i != path.lastIndex) {
                if (abs(path[i].x - path[i + 1].x) + abs(path[i].y -  path[i + 1].y) > 1) {
                    throw IllegalStateException("Distance between obstacle points > 1: ${path[i]} and ${path[i + 1]}")
                }
                if (path[i + 1].time - path[i].time != 1L) {
                    throw IllegalStateException("Time interval between steps > 1: ${path[i]} and ${path[i + 1]}")
                }
            }
        }
    }

    @Test
    fun test_simple_dij() {
        val map = """
        ............#######......
        ............#######......
        ....####.................
        .............#####.......
        .........................
        ....#####################
        .........................
        .........................
        """.trimIndent()
        val test2 = SingleBotCase(CaseMap(map), Point(24, 0), Point(0, 7), emptyList())

        val resDij = DijkstraWithTimeDimension().findPath(test2).path
        val resSipp = SIPP().findPath(test2).path

        checkPath(test2, resDij!!)
        checkPath(test2, resSipp!!)

        assert(resDij.size == resSipp.size)

        println("Dijkstra:")
        test2.printWithPath(resDij!!)

        println("single.SIPP:")
        test2.printWithPath(resSipp!!)
    }

    @Test
    fun test_obstacles_simple_dij() {
        val map = """
        ............#######......
        ............#######......
        ....####.................
        .............#####.......
        .........................
        ....#####################
        .........................
        .........................
        """.trimIndent()

        val obstacle1 = listOf(TimePoint(23, 0, 0), TimePoint(23, 0, 1), TimePoint(23, 1, 2), TimePoint(23, 2, 3), TimePoint(23, 3, 4), TimePoint(23, 4, 5))
        val obstacle2 = listOf(TimePoint(21, 0, 0), TimePoint(22, 0, 1), TimePoint(23, 0, 2), TimePoint(23, 1, 3))
        val test2 = SingleBotCase(CaseMap(map), Point(24, 0), Point(0, 7), listOf(Obstacle(obstacle1, 0.1), Obstacle(obstacle2, 0.2)))

        val resDij = DijkstraWithTimeDimension().findPath(test2).path!!
        val resSipp = SIPP().findPath(test2).path!!

        assert(resDij.size == resSipp.size)

        println("Dijkstra:")
        test2.printWithPath(resDij)

        println("single.SIPP:")
        test2.printWithPath(resSipp)
    }

    @Test
    fun test_moscow_dij() {
        val test = SingleBotCase.fromFile("tests/Moscow_2_256.map", "tests/Moscow_2_256", Point(40, 70), Point(0, 0))

        Timer.start()
        val res = DijkstraWithTimeDimension().findPath(test).path!!
        println("Dijkstra time: ${Timer.get()}")

        checkPath(test, res)

        test.printWithPath(res)
    }

    @Test
    fun test_moscow_sipp() {
        val test = SingleBotCase.fromFile("tests/Moscow_2_256.map", "tests/Moscow_2_256", Point(40, 70), Point(0, 0))

        Timer.start()
        val res = SIPP().findPath(test).path!!
        println("Sipp time: ${Timer.get()}")

        checkPath(test, res)

        test.printWithPath(res)

        test.emulateToFile(res, "moscow_steps.txt")
    }

    @Test
    fun test_failed_path_dij() {
        val map = """
        ............#######......
        ............#######......
        ....####.................
        .............#####.......
        .........................
        ....#####################
        .........................
        .........................
        """.trimIndent()

        val obstacle1 = listOf(TimePoint(23, 0, 0), TimePoint(24, 0, 1))
        val obstacle2 = listOf(TimePoint(24, 1, 0), TimePoint(24, 0, 1))
        val test2 = SingleBotCase(CaseMap(map), Point(24, 0), Point(0, 7), listOf(Obstacle(obstacle1, 0.1), Obstacle(obstacle2, 0.1)))

        val res = DijkstraWithTimeDimension().findPath(test2).path

        //no path found
        assert(res == null)
    }

    @Test
    fun test_failed_path_sipp() {
        val map = """
        ............#######......
        ............#######......
        ....####.................
        .............#####.......
        .........................
        ....#####################
        .........................
        .........................
        """.trimIndent()

        val obstacle1 = listOf(TimePoint(23, 0, 0), TimePoint(24, 0, 1))
        val obstacle2 = listOf(TimePoint(24, 1, 0), TimePoint(24, 0, 1))
        val test2 = SingleBotCase(CaseMap(map), Point(24, 0), Point(0, 7), listOf(Obstacle(obstacle1, 0.1), Obstacle(obstacle2, 0.1)))

        val res = SIPP().findPath(test2).path

        assert(res == null)
    }
}