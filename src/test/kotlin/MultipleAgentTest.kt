import multiple.AgentsComparator
import multiple.MultiAgentCase
import multiple.MultipleAgentSolver
import org.junit.jupiter.api.Test
import single.DijkstraWithTimeDimension
import single.SIPP

class MultipleAgentTest {
    @Test
    fun simpleTest() {
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

        val test = MultiAgentCase(CaseMap(map),
            listOf(
                Pair(Point(24, 0), Point(0, 7)),
                Pair(Point(1, 7), Point(23, 0)),
                Pair(Point(0, 7), Point(24, 0))
            ),
            listOf()
        )

        val solverDij = MultipleAgentSolver(DijkstraWithTimeDimension(), AgentsComparator.QUICKEST_FIRST)
        val solverSipp = MultipleAgentSolver(SIPP(), AgentsComparator.QUICKEST_FIRST)

        val res1 = solverDij.solve(test).paths
        val res2 = solverSipp.solve(test).paths

        println("Dijkstra sum duration: ${res1!!.sumBy { it.size }}")
        println("SIPP sum duration: ${res2!!.sumBy { it.size }}")

        println("Dijkstra max duration: ${res1!!.maxOf { it.size }}")
        println("SIPP max duration: ${res2!!.maxOf { it.size }}")

        //test.emulate(res1!!)
    }

    @Test
    fun fromFile() {
        val test = MultiAgentCase.fromFile("tests/maze512-8-3.map", "tests/maze512-8-3", "tests/maze512-8-3.obs")

        //val solverDij = MultipleAgentSolver(DijkstraWithTimeDimension(), AgentsComparator.QUICKEST_FIRST)
        val solverSipp = MultipleAgentSolver(SIPP(), AgentsComparator.QUICKEST_FIRST)

        //val res1 = solverDij.solve(test)
        val res2 = solverSipp.solve(test).paths

        //println("Dijkstra sum duration: ${res1!!.sumBy { it.size }}")
        println("SIPP sum duration: ${res2!!.sumBy { it.size }}")

        //println("Dijkstra max duration: ${res1!!.maxOf { it.size }}")
        println("SIPP max duration: ${res2!!.maxOf { it.size }}")

        test.emulateToFile(res2, "multiple-steps.log")
    }
}